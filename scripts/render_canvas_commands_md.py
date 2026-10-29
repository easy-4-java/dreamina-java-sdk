#!/usr/bin/env python3
"""根据随包 CLI schema 重渲染 docs/DREAMINA_CANVAS_COMMANDS.md。

用法：
    uv run --no-project python scripts/render_canvas_commands_md.py [schema.json] [output.md]

不传参数时读取随包最新 schema 并覆盖 docs/DREAMINA_CANVAS_COMMANDS.md。
表格逐 flag 由 schema 的 description/constraints 生成；升级 CLI 时先以旧快照
round-trip 校验（生成结果应与既有文档一致）再渲染新版本。
"""
import json
import sys
import unicodedata
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
FOOTER = """## 全局参数与辅助命令

SDK 固定 `--format=json --non-interactive`；`profile`、`region` 来自配置。`yes(true)` 显式传 `--yes`。

`help()` / `help("node create")` 对应根/分组/命令帮助。`completion(shell, noDescriptions)` 支持
bash、zsh、fish、powershell，返回脚本文本。
"""


def load_schema(path=None):
    target = Path(path) if path else sorted((ROOT / 'src/main/resources/io/github/easy4j/dreamina/cli/opts').glob('schema-[0-9]*.json'))[-1]
    return json.loads(target.read_text(encoding='utf-8')), target


def walk(node, prefix, out):
    path = (prefix + ' ' + node['name']).strip()
    out.append((path, node))
    for child in node.get('subcommands') or []:
        walk(child, path, out)


def display_width(text):
    return sum(2 if unicodedata.east_asian_width(ch) in 'WF' else 1 for ch in text)


def pad(text, width):
    return text + ' ' * max(0, width - display_width(text))


def table(head, cell_rows):
    widths = [max([display_width(h)] + [display_width(r[i]) for r in cell_rows]) for i, h in enumerate(head)]
    lines = ['| ' + ' | '.join(pad(h, w) for h, w in zip(head, widths)) + ' |']
    lines.append('|' + '|'.join('-' * (w + 2) for w in widths) + '|')
    for r in cell_rows:
        lines.append('| ' + ' | '.join(pad(c, w) for c, w in zip(r, widths)) + ' |')
    return lines


def cell(flag):
    parts = [flag.get('description') or flag['name']]
    if flag.get('constraints'):
        parts.append(flag['constraints'])
    return '；'.join(parts).replace('|', '\\|')


def render_flag_table(flags):
    rows = [('`%s`' % f['name'], str(f['type']).replace('|', '\\|'), '是' if f.get('required') else '否', str(f.get('default') or '—'), cell(f)) for f in flags]
    return table(['参数', '类型', '必填', '默认', '含义与约束'], rows)


def render_enum_table(routes):
    rows = [('`%s`' % '_'.join(w.upper() for w in p.replace('-', '_').split(' ')), '`%s`' % p, (n.get('summary') or '').replace('|', '\\|')) for p, n in routes]
    return table(['Java 枚举', 'CLI 路径', '用途'], rows)


def main():
    schema, target = load_schema(sys.argv[1] if len(sys.argv) > 1 else None)
    nodes = []
    for family in schema['subcommands']:
        walk(family, '', nodes)
    version = target.stem.replace('schema-', '')
    detail_routes = []
    for family in schema['subcommands']:
        if family.get('detailCommand'):
            if family['detailCommand'] != 'dreamina-canvas schema director':
                raise ValueError('Unresolved public detailCommand: ' + family['detailCommand'])
            detail = json.loads((target.parent / ('schema-director-' + version + '.json')).read_text())
            director = next(node for node in detail['subcommands'] if node['name'] == 'director')
            for child in director['subcommands']:
                walk(child, 'director', nodes)
            root_schema = next(node for node in schema['subcommands'] if node['name'] == 'schema')
            detail_routes.append(('schema director', dict(root_schema, name='director', summary='返回导演台独立公开契约')))
    nodes.extend(detail_routes)
    routes = [(p, n) for p, n in nodes if n.get('successData')]
    sections = []
    for path, node in routes:
        lines = ['## %s' % path, '']
        codes = node.get('declaredExitCodes') or []
        tail = ('。退出码：%s。' % ', '.join(str(c) for c in codes)) if codes else '。'
        lines.append((node.get('summary') or '') + tail)
        lines.append('')
        args = node.get('arguments') or []
        if args:
            named = '、'.join('%s（%s%s）%s' % (a['name'], '必填' if a.get('required') else '可选', ('；' + a['constraints']) if a.get('constraints') else '', '（可重复）' if a.get('repeatable') else '') for a in args)
            lines.append('位置参数：%s。' % named)
            lines.append('')
        if node.get('flags'):
            lines.extend(render_flag_table(node['flags']))
        else:
            lines.append('无命令级选项。')
        lines.append('')
        sections.append('\n'.join(lines))
    header = [
        '# Canvas CLI %s 完整业务命令目录' % version,
        '',
        ('来源：本机 `dreamina-canvas schema`、其声明的独立 detailCommand（当前为 `schema director`）与逐项 `--help`。' if detail_routes else '来源：本机 `dreamina-canvas schema` 与逐项 `--help`。') + '%d 个业务入口，%d 个命令/组描述。动态模型、音色不固化到枚举。完整' % (len(routes), len(nodes)),
        'successData/dryRunData/constraints 见随包 schema。',
        '',
    ]
    header.extend(render_enum_table(routes))
    body = '\n'.join(header) + '\n\n' + '\n'.join(sections) + '\n' + FOOTER
    out = ROOT / 'docs/DREAMINA_CANVAS_COMMANDS.md'
    if len(sys.argv) > 2:
        out = Path(sys.argv[2])
    out.write_text(body, encoding='utf-8')
    print('routes=%d nodes=%d -> %s' % (len(routes), len(nodes), out))


if __name__ == '__main__':
    main()
