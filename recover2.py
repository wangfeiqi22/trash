#!/usr/bin/env python3
import subprocess, os, re, json

base = "/Users/ripple/trash"
out_dir = f"{base}/.recovered_blobs"
os.makedirs(out_dir, exist_ok=True)

result = subprocess.run(["git", "-C", base, "fsck", "--lost-found"], capture_output=True, text=True)
blobs = [line.split()[2] for line in result.stdout.splitlines() if "dangling blob" in line]
print(f"Checking {len(blobs)} blobs...")

# We already recovered many Java files. Let's look for:
# 1. Vue view files
# 2. Router config (real one)
# 3. package.json (frontend)
# 4. pom.xml
# 5. vitest.config.mjs
# 6. main.js
# 7. Index.html
# 8. Docs
# 9. Integration tests
# 10. Other components

found = {}

for i, blob in enumerate(blobs):
    try:
        result = subprocess.run(["git", "-C", base, "cat-file", "-p", blob], capture_output=True, timeout=5)
        content = result.stdout.decode('utf-8', errors='ignore')
        if len(content) < 50:
            continue
    except:
        continue

    first_line = content.strip().split('\n')[0]

    # === Vue files with <script setup> - need to check more carefully ===
    if '<template>' in content and '<script setup>' in content:
        # Check for Vue view names in the component definition
        name_patterns = [
            (r'const\s+LoginView\s*=', 'LoginView.vue'),
            (r'const\s+HomeDashboardView\s*=', 'HomeDashboardView.vue'),
            (r'const\s+AdminDashboardView\s*=', 'AdminDashboardView.vue'),
            (r'const\s+DriverDashboardView\s*=', 'DriverDashboardView.vue'),
            (r'const\s+PropertyDashboardView\s*=', 'PropertyDashboardView.vue'),
            (r'const\s+StationDashboardView\s*=', 'StationDashboardView.vue'),
            (r'const\s+DriverTaskView\s*=', 'DriverTaskView.vue'),
            (r'const\s+DriverProfileView\s*=', 'DriverProfileView.vue'),
            (r'const\s+AdminUserAuditView\s*=', 'AdminUserAuditView.vue'),
            (r'const\s+ChatView\s*=', 'ChatView.vue'),
        ]
        for pattern, filename in name_patterns:
            if re.search(pattern, content):
                fp = f"{base}/frontend/src/views/{filename}"
                if not os.path.exists(fp):
                    os.makedirs(os.path.dirname(fp), exist_ok=True)
                    with open(fp, 'w', encoding='utf-8') as f:
                        f.write(content)
                    print(f"  [VUE]  views/{filename}")
                    found[filename] = True
                    break
        continue

    # === Vue files with Options API (<script> but not <script setup>) ===
    if '<template>' in content and "<script>\n" in content:
        name_patterns = [
            ('AppHeader', 'AppHeader.vue'),
            ('AppSidebar', 'AppSidebar.vue'),
            ('AiChatWidgetAvatar', 'AiChatWidgetAvatar.vue'),
            ('StationCard', 'StationCard.vue'),
            ('OrderCard', 'OrderCard.vue'),
            ('OrderTimeline', 'OrderTimeline.vue'),
        ]
        for name, filename in name_patterns:
            if f'"{name}"' in content or f"'{name}'" in content or f'export default {{ name:' in content:
                fp = f"{base}/frontend/src/components/{filename}"
                if not os.path.exists(fp):
                    os.makedirs(os.path.dirname(fp), exist_ok=True)
                    with open(fp, 'w', encoding='utf-8') as f:
                        f.write(content)
                    print(f"  [VUE]  components/{filename}")
                    found[filename] = True
                    break
        continue

    # === Router config - createRouter with routes array ===
    if ('createRouter' in content or 'createWebHistory' in content) and 'routes:' in content and 'path:' in content:
        fp = f"{base}/frontend/src/router/index.js"
        if not os.path.exists(fp) and not found.get('router/index.js'):
            os.makedirs(os.path.dirname(fp), exist_ok=True)
            with open(fp, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  [JS]   router/index.js (createRouter found)")
            found['router/index.js'] = True
        continue

    # === main.js ===
    if "createApp" in content and ("mount('#app')" in content or "mount('#app')" in content) and "App" in content:
        fp = f"{base}/frontend/src/main.js"
        if not os.path.exists(fp):
            os.makedirs(os.path.dirname(fp), exist_ok=True)
            with open(fp, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  [JS]   main.js")
            found['main.js'] = True
        continue

    # === App.vue (should be separate from router) ===
    if content.strip().startswith('<template>') and '<router-view' in content and '<script setup>' in content:
        fp = f"{base}/frontend/src/App.vue"
        if not os.path.exists(fp) and 'createApp' not in content:
            with open(fp, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  [VUE]  App.vue (router-view found)")
            found['App.vue'] = True
        continue

    # === vitest.config.mjs ===
    if ('vitest' in content.lower() and 'environment:' in content and 'happy-dom' in content) or \
       ('vitest/config' in content):
        fp = f"{base}/frontend/vitest.config.mjs"
        if not os.path.exists(fp):
            os.makedirs(os.path.dirname(fp), exist_ok=True)
            with open(fp, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  [JS]   vitest.config.mjs")
            found['vitest.config.mjs'] = True
        continue

    # === package.json (frontend) ===
    if '"name":' in content and ('"vue":' in content or '"vite":' in content):
        fp = f"{base}/frontend/package.json"
        if not os.path.exists(fp):
            os.makedirs(os.path.dirname(fp), exist_ok=True)
            with open(fp, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  [JSON] frontend/package.json")
            found['frontend/package.json'] = True
        continue

    # === pom.xml ===
    if '<project' in content and '</project>' in content and ('<groupId>com.renewable.ai</groupId>' in content or '<groupId>com.renewable</groupId>' in content):
        fp = f"{base}/backend/pom.xml"
        if not os.path.exists(fp):
            os.makedirs(os.path.dirname(fp), exist_ok=True)
            with open(fp, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  [XML]  backend/pom.xml")
            found['pom.xml'] = True
        continue

    # === index.html ===
    if '<!DOCTYPE html>' in content or '<html' in content[:200]:
        if 'id="app"' in content or 'src="/src/main.js"' in content:
            fp = f"{base}/frontend/index.html"
            if not os.path.exists(fp):
                with open(fp, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"  [HTML] frontend/index.html")
                found['index.html'] = True
            continue

    # === application-dev.yml / test / prod ===
    if re.search(r'^server:', content, re.MULTILINE) and 'jwt:' in content:
        for suffix, fpname in [('dev', 'application-dev.yml'), ('test', 'application-test.yml'), ('prod', 'application-prod.yml')]:
            if suffix in content:
                fp = f"{base}/backend/src/main/resources/{fpname}"
                if not os.path.exists(fp):
                    os.makedirs(os.path.dirname(fp), exist_ok=True)
                    with open(fp, 'w', encoding='utf-8') as f:
                        f.write(content)
                    print(f"  [YAML] {fpname}")
                    found[fpname] = True
                break
        continue

    # === Markdown docs ===
    if re.match(r'^#\s+', content) and len(content) > 200:
        title_m = re.search(r'^#\s+(.+?)\n', content)
        title = title_m.group(1) if title_m else f"doc_{i}"
        safe_title = re.sub(r'[^\w\-]', '_', title)[:50]

        if 'Tracker' in content and ('## Task' in content or '| Status |' in content):
            fp = f"{base}/docs/plans/2026-05-03-smart-waste-remediation-tracker.md"
        elif 'Remediation' in content and 'Plan' in content:
            fp = f"{base}/docs/plans/2026-05-03-smart-waste-remediation-plan.md"
        elif 'Design' in content or '架构' in content:
            fp = f"{base}/docs/plans/2026-05-03-smart-waste-remediation-design.md"
        elif 'Runbook' in content or 'release' in content.lower():
            fp = f"{base}/docs/ops/2026-05-03-release-runbook.md"
        elif 'initialize' in content.lower() or 'setup' in content.lower():
            fp = f"{base}/docs/ops/2026-05-03-initialization-guide.md"
        elif 'Logging' in content or '日志' in content:
            fp = f"{base}/docs/standards/2026-05-03-logging-standard.md"
        elif 'README' in content or ('## ' in content and len(content) > 2000):
            fp = f"{base}/README.md"
        else:
            fp = f"{out_dir}/{safe_title}.md"

        if not os.path.exists(f"{base}/{fp}") and not any(x in fp for x in ['recovered']):
            os.makedirs(os.path.dirname(f"{base}/{fp}"), exist_ok=True)
            with open(f"{base}/{fp}", 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  [MD]   {os.path.basename(fp)}")
            found[f'doc:{fp}'] = True
        continue

    # === Integration test Java files ===
    pkg_match = re.search(r'^package ([\w.]+);', content, re.MULTILINE)
    class_match = re.search(r'^public class (\w+)', content, re.MULTILINE)
    if pkg_match and class_match:
        pkg = pkg_match.group(1)
        cls = class_match.group(1)
        if 'IntegrationTest' in cls or 'ControllerTest' in cls or 'Test' in cls:
            fp = f"{base}/backend/src/test/java/com/renewable/ai/{pkg.replace('.', '/')}/{cls}.java"
            if not os.path.exists(fp):
                os.makedirs(os.path.dirname(fp), exist_ok=True)
                with open(fp, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"  [JAVA] test/{cls}.java")
                found[f'test:{cls}'] = True

    # === Save all blobs for manual inspection ===
    blob_file = f"{out_dir}/blob_{i}_{blob[:12]}.txt"
    with open(blob_file, 'w', encoding='utf-8', errors='ignore') as f:
        f.write(f"# Blob: {blob}\n# First line: {first_line[:100]}\n# Length: {len(content)}\n\n")
        f.write(content)

print(f"\n=== Phase 2 Recovery Summary ===")
print(f"  Files found in phase 2: {len(found)}")
print(f"  Blobs dumped for inspection: {len(blobs)}")
print(f"  Check .recovered_blobs/ for all blob contents")
