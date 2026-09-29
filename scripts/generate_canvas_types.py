#!/usr/bin/env python3
"""根据已审阅的 CLI 1.0.1 契约生成具体 Java 类型。运行后必须执行字段覆盖测试。"""
import json, re
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
BASE=ROOT/'src/main/java/io/github/easy4j/dreamina/cli'
SCHEMA=json.loads((ROOT/'src/main/resources/io/github/easy4j/dreamina/cli/opts/schema-1.0.1.json').read_text())
PRIMITIVES={'string':'String','integer':'Long','number':'java.math.BigDecimal','boolean':'Boolean','duration':'java.time.Duration'}
def pas(s): return ''.join(w[0].upper()+w[1:] for w in re.split(r'[- _]',s))
def java(s): return {'default':'defaultValue','wait':'waitForCompletion','clear-auxiliary':'clearAuxiliaryBindings'}.get(s,s.split('-')[0]+''.join(pas(x) for x in s.split('-')[1:]))
def key(fields): return json.dumps([{k:v for k,v in f.items() if k not in ('optional','description')} for f in fields],sort_keys=True)
classes={}; shapes={}; command_types={}
def obj(fields,name,force=False):
    signature=key(fields)
    if signature in shapes and not force: return shapes[signature]
    if name in classes and key(classes[name])!=signature: raise ValueError(name)
    shapes.setdefault(signature,name); classes[name]=fields
    return name
# 固定递归结构；禁止回退到 Object / JSON 树。
schema_cmd=next(c for c in SCHEMA['subcommands'] if c['name']=='schema')['successData']['fields']
cmd_fields=next(f for f in schema_cmd if f['name']=='subcommands')['fields']
field_fields=next(f for f in cmd_fields if f['name']=='successData')['fields'][1]['fields']
obj(schema_cmd,'DreaminaCanvasSchema',True)
obj(cmd_fields,'DreaminaCanvasSchemaCommand',True)
obj(field_fields,'DreaminaCanvasSchemaField',True)
obj(next(f for f in schema_cmd if f['name']=='globalFlags')['fields'],'DreaminaCanvasSchemaFlag',True)
def findrule(x):
    if isinstance(x,dict):
        if x.get('name')=='rule' and x.get('fields'): return x['fields']
        for v in x.values():
            r=findrule(v)
            if r:return r
    if isinstance(x,list):
        for v in x:
            r=findrule(v)
            if r:return r
obj(findrule(SCHEMA),'DreaminaCanvasParameterRule',True)
def merge(a,b):
    out={f['name']:dict(f) for f in a}
    for f in b:
        if f['name'] in out:
            old=out[f['name']]
            assert old['type']==f['type'],(old,f)
            if 'fields' in f:old['fields']=merge(old.get('fields',[]),f['fields'])
        else:out[f['name']]=dict(f)
    return list(out.values())
def walk(x,p=''):
    p=(p+' '+x.get('name','')).strip()
    if 'successData' in x:yield p,x
    for c in x.get('subcommands',[]):yield from walk(c,p)
commands=list(walk(SCHEMA))
for p,c in commands:
    fields=merge(c['successData']['fields'],c.get('dryRunData',{}).get('fields',[]))
    if p=='version': t='DreaminaVersion'
    elif p=='auth account': t='DreaminaLoginAccount'
    elif p=='auth logout': t='DreaminaLogout'
    elif p=='schema': t='DreaminaCanvasSchema'
    else: t=obj(fields,('DreaminaCanvas'+pas(p)+'Result').replace('DreaminaCanvasCanvas','DreaminaCanvas'),True)
    command_types[p]=t

def typefor(f,parent):
    typ=f['type']
    if typ in PRIMITIVES:return PRIMITIVES[typ]
    if typ.startswith('array<') and typ!='array<object>':return 'java.util.List<'+PRIMITIVES[typ[6:-1]]+'>'
    fields=f.get('fields',[])
    if not fields:
        t={'rule':'DreaminaCanvasParameterRule','fields':'DreaminaCanvasSchemaField','subcommands':'DreaminaCanvasSchemaCommand'}.get(f['name'])
        if not t:raise ValueError((parent,f))
    elif f['name']=='challenge':t='DreaminaDeviceLogin'
    else:
        stem=pas(f['name']);stem=stem[:-1] if stem.endswith('s') and not stem.endswith('ss') else stem
        name='DreaminaCanvas'+stem
        if name=='DreaminaCanvasError': name='DreaminaCanvasServiceError'
        if name in classes and key(classes[name])!=key(fields):
            name=parent+stem
            while name in classes and key(classes[name])!=key(fields): name += 'Value'
        t=obj(fields,name)
    if typ=='array<object>':return 'java.util.List<'+t+'>'
    if typ=='map<string,object>':return 'java.util.Map<String, '+t+'>'
    assert typ=='object',typ
    return t
# 先递归展开类型，随后每个类一个文件。
done=set()
while len(done)<len(classes):
    name=next(n for n in classes if n not in done)
    for f in classes[name]:typefor(f,name)
    done.add(name)
for name,fields in classes.items():
    lines=['package io.github.easy4j.dreamina.cli.model;','','import com.fasterxml.jackson.annotation.JsonIgnoreProperties;','import com.fasterxml.jackson.annotation.JsonProperty;','import lombok.Getter;','import lombok.Setter;','', '/** 画布 CLI 契约对象；由 scripts/generate_canvas_types.py 维护。 */','@Getter','@Setter','@JsonIgnoreProperties(ignoreUnknown = true)','public final class '+name+' {']
    for f in fields:
        lines+=['    /** 契约字段 {@code '+f['name']+'}。 */','    @JsonProperty("'+f['name']+'")','    private '+typefor(f,name)+' '+java(f['name'])+';']
    lines+=['}','']
    (BASE/'model'/f'{name}.java').write_text('\n'.join(lines))
for p,c in commands:
    name=('DreaminaCanvas'+pas(p)+'Request').replace('DreaminaCanvasCanvas','DreaminaCanvas'); t=command_types[p]
    lines=['package io.github.easy4j.dreamina.cli.opts;','','import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;','import io.github.easy4j.dreamina.cli.model.'+t+';','import lombok.Builder;','import lombok.Getter;','', '/** '+c['summary']+'。仅显式参数进入命令，不自动运行或批准积分。 */','@Getter','@Builder','public final class '+name+' extends DreaminaCanvasRequest<'+t+'> {']
    entries=[]
    for f in c.get('flags',[]):entries.append((f,False))
    for f in c.get('arguments',[]):entries.append((dict(f,type='string[]' if f.get('repeatable') else 'string'),True))
    for f,pos in entries:
        ty='java.util.List<String>' if f['type']=='string[]' else PRIMITIVES[f['type']]
        lines+=['    /** '+f.get('description',f['name'])+'；'+('位置参数' if pos else '--'+f['name'])+'。 */','    @DreaminaCanvasParameter(value = "'+f['name']+'", positional = '+str(pos).lower()+')',*((['    @lombok.Singular("add'+pas(java(f['name']))+'")'] if f['type']=='string[]' else [])), '    private final '+ty+' '+java(f['name'])+';']
    lines+=['    /** 显式启用确认策略，不替代积分凭证。 */','    private final boolean yes;','','    @Override public DreaminaCanvasCommand getCommand() { return DreaminaCanvasCommand.'+p.upper().replace(' ','_')+'; }','    @Override public Class<'+t+'> getDataType() { return '+t+'.class; }','','    @Override protected DreaminaCanvasArguments arguments() {','        DreaminaCanvasArguments args = new DreaminaCanvasArguments();']
    for f,pos in entries:
        lines+=['        args.'+('position' if pos else 'flag')+'('+('' if pos else '"'+f['name']+'", ')+java(f['name'])+');']
    lines+=['        args.yes(yes);','        return args;','    }']
    overloads=[]
    for f,pos in entries:
        n=f['name']
        if n=='ratio':overloads+=['        /** 复用既有画幅枚举。 */','        public '+name+'Builder ratio(DreaminaRatio value) { this.ratio = java.util.Objects.isNull(value) ? null : value.getCliValue(); return this; }','        public '+name+'Builder ratio(String value) { this.ratio = value; return this; }']
        if n=='resolution':
            et='DreaminaImageResolutionType' if p.endswith('image') else 'DreaminaVideoResolutionType'
            overloads+=['        /** 复用既有分辨率枚举，转换 Canvas 大写 K。 */','        public '+name+'Builder resolution('+et+' value) { this.resolution = java.util.Objects.isNull(value) ? null : value.getCliValue().replace("k", "K"); return this; }','        public '+name+'Builder resolution(String value) { this.resolution = value; return this; }']
    if overloads:lines+=['    public static class '+name+'Builder {']+overloads+['    }']
    lines+=['}','']
    (BASE/'opts'/f'{name}.java').write_text('\n'.join(lines).replace('java.util.Objects','Objects').replace('import lombok.Getter;','import lombok.Getter;\nimport java.util.Objects;'))
# 生成逐指令入口，避免手工漏挂载新命令。
methods=[]
for p,c in commands:
    req=('DreaminaCanvas'+pas(p)+'Request').replace('DreaminaCanvasCanvas','DreaminaCanvas'); rt=command_types[p];method=pas(p);method=method[0].lower()+method[1:]
    methods+=['    /** '+c['summary']+'。 */','    public DreaminaCanvasResponse<'+rt+'> '+method+'('+req+' request) {','        return execute(request);','    }']
(ROOT/'scripts/canvas_executor_methods.txt').write_text('\n'.join(methods)+'\n')
(ROOT/'docs/validation/dreamina-canvas-types.json').write_text(json.dumps({'schemaVersion':'1','cliVersion':'1.0.1','commands':[{'command':p,'request':('DreaminaCanvas'+pas(p)+'Request').replace('DreaminaCanvasCanvas','DreaminaCanvas'),'data':command_types[p]} for p,c in commands]},indent=2)+'\n')
print('Generated',len(commands),'requests,',len(classes),'response objects')
