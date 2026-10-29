#!/usr/bin/env python3
"""从固定契约生成逐指令矩阵；真实结果仅来自显式的 SDK 证据，不把 mock 当作实机成功。"""
import argparse
import hashlib
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'docs/validation/dual-cli'
LEGACY = {'version':'versionInfo','help':'helpInfo','login':'loginInfo','login checklogin':'checkLoginInfo',
          'logout':'logoutInfo','relogin':'reloginInfo','user_credit':'userCreditInfo','list_task':'listTaskInfo',
          'query_result':'queryResultInfo','session':'sessionInfo','session create':'sessionCreateInfo',
          'session list':'sessionListInfo','session search':'sessionSearchInfo','session rename':'sessionRenameInfo',
          'session delete':'sessionDelete','text2image':'text2ImageSubmit','image2image':'image2ImageSubmit',
          'text2video':'text2VideoSubmit','image2video':'image2VideoSubmit','frames2video':'frames2VideoSubmit',
          'multiframe2video':'multiframe2VideoSubmit','multimodal2video':'multimodal2VideoSubmit','image_upscale':'imageUpscaleSubmit'}


def walk(node, prefix=''):
    name = (prefix + ' ' + node.get('name', '')).strip()
    if 'successData' in node:
        yield name, node
    for child in node.get('subcommands', []):
        yield from walk(child, name)


def source_fingerprint(root):
    digest = hashlib.sha256()
    for path in sorted((root / 'src/main').rglob('*')):
        if path.is_file():
            digest.update(str(path.relative_to(root)).encode())
            digest.update(b'\0')
            digest.update(path.read_bytes())
    digest.update((root / 'pom.xml').read_bytes())
    return digest.hexdigest()


def regression_verified(directory):
    if not directory or not (directory / 'regression.json').is_file():
        return False
    proof = json.loads((directory / 'regression.json').read_text())
    lines = proof.get('lines', [])
    return (proof.get('sourceFingerprint') == source_fingerprint(ROOT)
            and {line.get('java') for line in lines} == {8, 17, 21}
            and all(line.get('buildSuccess') is True and line.get('failures') == 0
                    and line.get('errors') == 0 and line.get('coveragePassed') is True
                    and line.get('tests', 0) > 0 for line in lines))


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--evidence-dir', type=Path)
    args = parser.parse_args()
    observations = {}
    if args.evidence_dir:
        for filename in ['legacy-live.jsonl', 'canvas-live.jsonl']:
            file = args.evidence_dir / filename
            if file.exists():
                for line in file.read_text().splitlines():
                    row = json.loads(line)
                    observations.setdefault((row['cli'], row['command']), []).append(row)
        blocked = args.evidence_dir / 'legacy-session-blocked.json'
        if blocked.exists():
            row = json.loads(blocked.read_text())
            for command in ([] if row.get('resolvedAfterFreshLogin') else ['session list', 'session search', 'session create', 'session rename', 'session delete']):
                observations.setdefault(('dreamina', command), []).append(dict(row, command=command))
    blocks = []
    if args.evidence_dir and (args.evidence_dir / 'blocked-scenarios.json').exists():
        blocks = json.loads((args.evidence_dir / 'blocked-scenarios.json').read_text())
    local_verified = regression_verified(args.evidence_dir)
    schema = json.loads((ROOT/'src/main/resources/io/github/easy4j/dreamina/cli/opts/schema-1.2.0.json').read_text())
    types = {x['command']:x for x in json.loads((ROOT/'docs/validation/dreamina-canvas-types.json').read_text())['commands']}
    director_schema = json.loads((ROOT/'src/main/resources/io/github/easy4j/dreamina/cli/opts/schema-director-1.2.0.json').read_text())
    types.update({x['command']: x for x in json.loads((ROOT/'docs/validation/dreamina-director-types.json').read_text())['commands']})
    cases = json.loads((ROOT/'src/test/resources/cli-contract/dual-cli-static-cases.json').read_text())
    rows = []
    for route, spec in list(walk(schema)) + list(walk(director_schema)):
        rows.append({'cli':'dreamina-canvas','version':'1.2.0','command':route,
                     'sdk':types[route], 'parameters':spec.get('flags',[]), 'arguments':spec.get('arguments',[]),
                     'response':spec['successData'], 'declaredExitCodes':spec.get('declaredExitCodes',[]),
                     'localTests':['DreaminaCanvasFieldCoverageTest','DreaminaCanvasCommandCoverageTest'],
                     'staticCases':[c['name'] for c in cases if c['command']==route.upper().replace(' ','_').replace('-','_')],
                     'recovery':'preserve original project/node/submit identity, raw output and structured error; never auto-resubmit'})
    schema_row = next(row for row in rows if row['command'] == 'schema')
    rows.append(dict(schema_row, command='schema director', sdk={'request': 'DreaminaCanvasSchemaDirectorRequest', 'data': 'DreaminaCanvasSchema'}))
    for route, method in [('help','help'),('completion bash','completionBash'),('completion zsh','completionZsh'),('completion fish','completionFish'),('completion powershell','completionPowershell')]:
        rows.append({'cli':'dreamina-canvas','version':'1.2.0','command':route,'sdk':method,
                     'localTests':['DreaminaCanvasInstalledCliTest'],'response':'text output','auxiliary':True,
                     'recovery':'not applicable; local helper command'})
    flags = json.loads((ROOT/'docs/validation/dreamina-1.4.21/flags.json').read_text())
    for route, method in LEGACY.items():
        rows.append({'cli':'dreamina','version':'1.4.21','command':route,'sdk':method,
                     'parameters':flags[route], 'helpEvidence':'docs/validation/dreamina-1.4.21/'+route.replace(' ','-')+'.txt',
                     'localTests':['DreaminaCliHelpSnapshotTest','DreaminaCliExecutorCommandsMockTest','DreaminaCliExecutorOverloadsMockTest'],
                     'recovery':'preserve submit_id and partialResult; query original task rather than resubmit'})
    for row in rows:
        observed = observations.get((row['cli'],row['command']),[])
        row['liveEvidence'] = observed
        row['scenarioGaps'] = [b for b in blocks if b['cli'] == row['cli'] and row['command'] in b['commands']]
        route = row['command']
        if row['cli'] == 'dreamina-canvas' and route.startswith('director '):
            row['recovery'] = ('preserve the original delivery operationRef path and use director resume-export; never route director paths to operation status/wait' if route in ['director export', 'director resume-export'] else
                               'preserve work file and pending write identity; interrupted create/apply writes resume with director push --file; version conflicts require pulling latestVersionId into a new file and replaying intended edits without force')
        elif row['cli'] == 'dreamina-canvas' and route.startswith('node delete'):
            row['recovery'] = 'preserve update-id and exact node IDs; query node delete status; do not repeat ambiguous deletion or broaden the approved batch'
        row['validationBoundary'] = 'Java validates declared syntax and static combinations; official CLI validates dynamic model eligibility, resource state, permissions and billing'
        if row['cli'] == 'dreamina':
            help_file = ROOT / row['helpEvidence']
            row['documentedDefaults'] = [line.strip() for line in help_file.read_text().splitlines() if re.search(r'default|默认', line, re.I)] if help_file.exists() else []
            row['response'] = 'DreaminaGenerateSubmit' if row['sdk'].endswith('Submit') else 'existing command-specific response; raw stdout/stderr retained'
            row['validationBoundary'] += '; legacy additionalArgs remains an explicit raw-argument escape hatch'

        row['localStatus'] = 'regression-passed' if local_verified else 'pending-current-regression'
        row['installedStatus'] = 'not-recorded'
        if local_verified and row['cli'] == 'dreamina-canvas':
            row['installedStatus'] = 'help-schema-verified'
            if row.get('auxiliary') or row['command'] in ['version', 'schema']:
                row['installedStatus'] = 'helper-executed'
            elif any(flag.get('name') == 'dry-run' for flag in row.get('parameters', [])):
                row['installedStatus'] = 'dry-run-executed'
        row['liveStatus'] = 'verified' if any(x.get('success') is True for x in observed) else ('recovered-success' if any(x.get('businessOutcome') == 'completed-via-original-file' for x in observed) else ('blocked' if row['scenarioGaps'] or observed else 'pending'))
        if row['liveStatus'] == 'blocked' and row['scenarioGaps'] and all(gap.get('classification') == 'pending-approval' for gap in row['scenarioGaps']):
            row['liveStatus'] = 'pending-approval'
        if row['scenarioGaps'] and row['liveStatus'] == 'verified':
            row['liveStatus'] = 'verified-with-scenario-gaps'
    OUT.mkdir(parents=True, exist_ok=True)
    (OUT/'commands.json').write_text(json.dumps(rows,ensure_ascii=False,indent=2)+'\n')
    lines = ['# 双 CLI 逐指令验收矩阵','',
             '本地字段/argv/mock、安装态 help/dry-run 和真实业务证据分开记录。没有真实成功证据的指令不标记完成。','',
             '| CLI | 指令 | SDK | 本地状态 | 安装态 | 真实状态 |','|---|---|---|---|---|---|']
    for row in rows:
        sdk = row['sdk']['request'] if isinstance(row['sdk'],dict) else row['sdk']
        lines.append(f"| {row['cli']} | `{row['command']}` | `{sdk}` | {row['localStatus']} | {row['installedStatus']} | {row['liveStatus']} |")
    (OUT/'commands.md').write_text('\n'.join(lines)+'\n')
    print('commands',len(rows),'real successes',sum(x['liveStatus']=='verified' for x in rows),'blocked',sum(x['liveStatus']=='blocked' for x in rows))


if __name__ == '__main__':
    main()
