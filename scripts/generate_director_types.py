#!/usr/bin/env python3
"""增量生成 director 独立契约，保留已有 Canvas 类型与手工修复。"""
import json
import re
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
BASE = ROOT / 'src/main/java/io/github/easy4j/dreamina/cli'
SCHEMA = json.loads((ROOT / 'src/main/resources/io/github/easy4j/dreamina/cli/opts/schema-director-1.2.0.json').read_text())

def pas(value):
    return ''.join(word[0].upper() + word[1:] for word in re.split('[- _]', value))

def java(value):
    return value.split('-')[0] + ''.join(pas(word) for word in value.split('-')[1:])

models = {}
def datatype(field, parent):
    primitive = {'string': 'String', 'integer': 'Long', 'boolean': 'Boolean', 'number': 'java.math.BigDecimal'}
    kind = field['type']
    if kind in primitive:
        return primitive[kind]
    if kind == 'array<string>':
        return 'java.util.List<String>'
    # 官方 document/raw 及未公布内部字段的对象按 JSON 原样保留。
    if kind == 'raw' or not field.get('fields'):
        return 'java.util.List<DreaminaCanvasDirectorJsonValue>' if kind == 'array<object>' else 'DreaminaCanvasDirectorJsonValue'
    name = parent + pas(field['name'])
    models[name] = field['fields']
    return 'java.util.List<' + name + '>' if kind == 'array<object>' else name

commands = SCHEMA['subcommands'][0]['subcommands']
manifest = []
methods = []
for command in commands:
    suffix = 'Director' + pas(command['name'])
    result = 'DreaminaCanvas' + suffix + 'Result'
    request = 'DreaminaCanvas' + suffix + 'Request'
    models[result] = command['successData']['fields']
    lines = ['package io.github.easy4j.dreamina.cli.opts;', '', 'import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;', 'import io.github.easy4j.dreamina.cli.model.' + result + ';', 'import lombok.Builder;', 'import lombok.Getter;', '', '/** ' + command['summary'] + '；保留官方默认值，仅传显式参数。 */', '@Getter', '@Builder', 'public final class ' + request + ' extends DreaminaCanvasRequest<' + result + '> {']
    for field in command['flags']:
        lines += ['    /** ' + field['description'] + '；--' + field['name'] + '。 */', '    @DreaminaCanvasParameter(value = "' + field['name'] + '", positional = false)', '    private final ' + datatype(field, result) + ' ' + java(field['name']) + ';']
    lines += ['    /** 显式确认策略，不替代任何服务端授权。 */', '    private final boolean yes;', '    @Override', '    public DreaminaCanvasCommand getCommand() {', '        return DreaminaCanvasCommand.DIRECTOR_' + command['name'].upper().replace('-', '_') + ';', '    }', '    @Override', '    public Class<' + result + '> getDataType() {', '        return ' + result + '.class;', '    }', '    @Override', '    protected DreaminaCanvasArguments arguments() {', '        DreaminaCanvasArguments args = new DreaminaCanvasArguments();']
    for field in command['flags']:
        lines += ['        args.flag("' + field['name'] + '", ' + java(field['name']) + ');']
    lines += ['        args.yes(yes);', '        return args;', '    }', '}', '']
    (BASE / 'opts' / (request + '.java')).write_text('\n'.join(lines))
    methods += ['    /** ' + command['summary'] + '。 */', '    public DreaminaCanvasResponse<' + result + '> director' + pas(command['name']) + '(' + request + ' request) {', '        return execute(request);', '    }']
    manifest.append({'command': 'director ' + command['name'], 'request': request, 'data': result})
completed = set()
while len(completed) < len(models):
    name = next(key for key in models if key not in completed)
    for field in models[name]:
        datatype(field, name)
    completed.add(name)
for name, fields in models.items():
    lines = ['package io.github.easy4j.dreamina.cli.model;', '', 'import com.fasterxml.jackson.annotation.JsonIgnoreProperties;', 'import com.fasterxml.jackson.annotation.JsonProperty;', 'import lombok.Getter;', 'import lombok.Setter;', '', '/** 官方导演台响应；由 generate_director_types.py 增量生成。 */', '@Getter', '@Setter', '@JsonIgnoreProperties(ignoreUnknown = true)', 'public final class ' + name + ' {']
    for field in fields:
        lines += ['    /** 官方字段 ' + field['name'] + '。 */', '    @JsonProperty("' + field['name'] + '")', '    private ' + datatype(field, name) + ' ' + java(field['name']) + ';']
    (BASE / 'model' / (name + '.java')).write_text('\n'.join(lines + ['}', '']))
(ROOT / 'scripts/director_executor_methods.txt').write_text('\n'.join(methods) + '\n')
(ROOT / 'docs/validation/dreamina-director-types.json').write_text(json.dumps({'cliVersion': '1.2.0', 'commands': manifest}, indent=2) + '\n')
print('Generated', len(commands), 'director requests and', len(models), 'responses')
