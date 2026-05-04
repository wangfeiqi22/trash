#!/usr/bin/env python3
import subprocess, os, re

base = "/Users/ripple/trash"

result = subprocess.run(["git", "-C", base, "fsck", "--lost-found"], capture_output=True, text=True)
blobs = []
for line in result.stdout.splitlines():
    if "dangling blob" in line:
        parts = line.split()
        if len(parts) >= 3:
            blobs.append(parts[2])

print(f"Total blobs: {len(blobs)}")

recovered_java = 0
recovered_vue = 0
recovered_js = 0
recovered_yaml = 0
recovered_xml = 0
recovered_md = 0
recovered_other = 0
skipped = 0

java_known_files = {
    'AiCustomerServiceApplication': 'backend/src/main/java/com/renewable/ai/AiCustomerServiceApplication.java',
    'StationService': 'backend/src/main/java/com/renewable/ai/service/StationService.java',
    'FleetService': 'backend/src/main/java/com/renewable/ai/service/FleetService.java',
    'KnowledgeBaseService': 'backend/src/main/java/com/renewable/ai/service/KnowledgeBaseService.java',
    'OrderService': 'backend/src/main/java/com/renewable/ai/service/OrderService.java',
    'UserService': 'backend/src/main/java/com/renewable/ai/service/UserService.java',
    'TokenService': 'backend/src/main/java/com/renewable/ai/service/TokenService.java',
    'FileValidationService': 'backend/src/main/java/com/renewable/ai/service/FileValidationService.java',
    'ResourceOwnershipService': 'backend/src/main/java/com/renewable/ai/service/ResourceOwnershipService.java',
    'AiService': 'backend/src/main/java/com/renewable/ai/service/AiService.java',
    'StationRepository': 'backend/src/main/java/com/renewable/ai/repository/StationRepository.java',
    'OrderRepository': 'backend/src/main/java/com/renewable/ai/repository/OrderRepository.java',
    'UserRepository': 'backend/src/main/java/com/renewable/ai/repository/UserRepository.java',
    'AiController': 'backend/src/main/java/com/renewable/ai/controller/AiController.java',
    'AuthController': 'backend/src/main/java/com/renewable/ai/controller/AuthController.java',
    'FileUploadController': 'backend/src/main/java/com/renewable/ai/controller/FileUploadController.java',
    'OrderController': 'backend/src/main/java/com/renewable/ai/controller/OrderController.java',
    'StationController': 'backend/src/main/java/com/renewable/ai/controller/StationController.java',
    'UserController': 'backend/src/main/java/com/renewable/ai/controller/UserController.java',
    'AuthenticationInterceptor': 'backend/src/main/java/com/renewable/ai/interceptor/AuthenticationInterceptor.java',
    'WebConfig': 'backend/src/main/java/com/renewable/ai/config/WebConfig.java',
    'JwtProperties': 'backend/src/main/java/com/renewable/ai/config/JwtProperties.java',
    'AiProperties': 'backend/src/main/java/com/renewable/ai/config/AiProperties.java',
    'CorsConfig': 'backend/src/main/java/com/renewable/ai/config/CorsConfig.java',
    'DataSeeder': 'backend/src/main/java/com/renewable/ai/config/DataSeeder.java',
    'GlobalExceptionHandler': 'backend/src/main/java/com/renewable/ai/common/GlobalExceptionHandler.java',
    'Result': 'backend/src/main/java/com/renewable/ai/common/Result.java',
    'Station': 'backend/src/main/java/com/renewable/ai/entity/Station.java',
    'Order': 'backend/src/main/java/com/renewable/ai/entity/Order.java',
    'User': 'backend/src/main/java/com/renewable/ai/entity/User.java',
    'OrderPhoto': 'backend/src/main/java/com/renewable/ai/entity/OrderPhoto.java',
    'OrderLog': 'backend/src/main/java/com/renewable/ai/entity/OrderLog.java',
    'Driver': 'backend/src/main/java/com/renewable/ai/entity/Driver.java',
    'Vehicle': 'backend/src/main/java/com/renewable/ai/entity/Vehicle.java',
    'DashboardStatsDTO': 'backend/src/main/java/com/renewable/ai/dto/DashboardStatsDTO.java',
    'AuthTokenDTO': 'backend/src/main/java/com/renewable/ai/dto/AuthTokenDTO.java',
    'LoginDTO': 'backend/src/main/java/com/renewable/ai/dto/LoginDTO.java',
    'FileUploadResponseDTO': 'backend/src/main/java/com/renewable/ai/dto/FileUploadResponseDTO.java',
}

vue_known_files = {
    'LoginView': 'frontend/src/views/LoginView.vue',
    'HomeDashboardView': 'frontend/src/views/HomeDashboardView.vue',
    'AdminDashboardView': 'frontend/src/views/AdminDashboardView.vue',
    'DriverDashboardView': 'frontend/src/views/DriverDashboardView.vue',
    'PropertyDashboardView': 'frontend/src/views/PropertyDashboardView.vue',
    'StationDashboardView': 'frontend/src/views/StationDashboardView.vue',
    'DriverTaskView': 'frontend/src/views/DriverTaskView.vue',
    'DriverProfileView': 'frontend/src/views/DriverProfileView.vue',
    'AdminUserAuditView': 'frontend/src/views/AdminUserAuditView.vue',
    'ChatView': 'frontend/src/views/ChatView.vue',
    'AppHeader': 'frontend/src/components/AppHeader.vue',
    'AppSidebar': 'frontend/src/components/AppSidebar.vue',
    'AiChatWidgetAvatar': 'frontend/src/components/AiChatWidgetAvatar.vue',
    'StationCard': 'frontend/src/components/StationCard.vue',
    'OrderCard': 'frontend/src/components/OrderCard.vue',
    'OrderTimeline': 'frontend/src/components/OrderTimeline.vue',
}

js_known_files = {
    'auth': 'frontend/src/stores/auth.js',
    'router': 'frontend/src/router/index.js',
    'api': 'frontend/src/api.js',
}

yaml_known_files = {
    'application': 'backend/src/main/resources/application.yml',
    'application-dev': 'backend/src/main/resources/application-dev.yml',
    'application-test': 'backend/src/main/resources/application-test.yml',
    'application-prod': 'backend/src/main/resources/application-prod.yml',
}

for i, blob in enumerate(blobs):
    try:
        result = subprocess.run(["git", "-C", base, "cat-file", "-p", blob], capture_output=True, timeout=5)
        content = result.stdout.decode('utf-8', errors='ignore')
        if not content or len(content) < 20:
            skipped += 1
            continue
    except Exception as e:
        skipped += 1
        continue

    # === JAVA FILES ===
    pkg_match = re.search(r'^package com\.renewable\.ai\.([\w.]+);', content, re.MULTILINE)
    class_match = re.search(r'^public (class|interface|enum) (\w+)', content, re.MULTILINE)
    if pkg_match and class_match:
        pkg = pkg_match.group(1)
        cls = class_match.group(2)
        if cls in java_known_files:
            fp = f"{base}/{java_known_files[cls]}"
        else:
            fp = f"{base}/backend/src/main/java/com/renewable/ai/{pkg.replace('.', '/')}/{cls}.java"
        if not os.path.exists(fp):
            os.makedirs(os.path.dirname(fp), exist_ok=True)
            with open(fp, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  [JAVA] {cls}.java ({pkg})")
            recovered_java += 1
        continue

    # === VUE FILES ===
    if ('<template>' in content or '<template>' in content[:200]) and \
       ('<script' in content):
        matched = False
        if '<script setup>' in content:
            name_m = re.search(r"const (\w+)View.*=", content)
            if name_m and name_m.group(1) in vue_known_files:
                fp = f"{base}/{vue_known_files[name_m.group(1)]}"
                if not os.path.exists(fp):
                    os.makedirs(os.path.dirname(fp), exist_ok=True)
                    with open(fp, 'w', encoding='utf-8') as f:
                        f.write(content)
                    print(f"  [VUE]  {name_m.group(1)}View.vue")
                    recovered_vue += 1
                    matched = True
        if not matched:
            for name, fp in vue_known_files.items():
                if not os.path.exists(f"{base}/{fp}") and name in content[:500]:
                    os.makedirs(os.path.dirname(f"{base}/{fp}"), exist_ok=True)
                    with open(f"{base}/{fp}", 'w', encoding='utf-8') as f:
                        f.write(content)
                    print(f"  [VUE]  {name}View.vue")
                    recovered_vue += 1
                    matched = True
                    break
        if matched:
            continue

    # === JS FILES ===
    if content.startswith('import'):
        matched = False
        if 'defineStore' in content or 'createPinia' in content:
            for name, fp in js_known_files.items():
                if name == 'auth' and ('defineStore' in content or 'localStorage' in content) and not os.path.exists(f"{base}/{fp}"):
                    os.makedirs(os.path.dirname(f"{base}/{fp}"), exist_ok=True)
                    with open(f"{base}/{fp}", 'w', encoding='utf-8') as f:
                        f.write(content)
                    print(f"  [JS]   stores/auth.js")
                    recovered_js += 1
                    matched = True
                    break
        if not matched:
            if 'createRouter' in content or "routes:" in content:
                fp = f"{base}/frontend/src/router/index.js"
                if not os.path.exists(fp):
                    os.makedirs(os.path.dirname(fp), exist_ok=True)
                    with open(fp, 'w', encoding='utf-8') as f:
                        f.write(content)
                    print(f"  [JS]   router/index.js")
                    recovered_js += 1
                    continue
            if 'axios' in content and 'localStorage' in content:
                fp = f"{base}/frontend/src/api.js"
                if not os.path.exists(fp):
                    os.makedirs(os.path.dirname(fp), exist_ok=True)
                    with open(fp, 'w', encoding='utf-8') as f:
                        f.write(content)
                    print(f"  [JS]   api.js")
                    recovered_js += 1
                    continue
            if "ORDER_STATUS" in content and "export" in content:
                fp = f"{base}/frontend/src/constants/orderStatus.js"
                if not os.path.exists(fp):
                    os.makedirs(os.path.dirname(fp), exist_ok=True)
                    with open(fp, 'w', encoding='utf-8') as f:
                        f.write(content)
                    print(f"  [JS]   constants/orderStatus.js")
                    recovered_js += 1
                    continue
        continue

    # === YAML FILES ===
    if re.match(r'^\w', content) and ('spring:' in content.lower() or 'servers:' in content or 'name:' in content):
        if re.search(r'(spring|database|server|ai|jwt)\s*[:&]', content, re.IGNORECASE):
            if 'application' in content and 'server:' in content:
                fp = f"{base}/backend/src/main/resources/application.yml"
                if not os.path.exists(fp):
                    os.makedirs(os.path.dirname(fp), exist_ok=True)
                    with open(fp, 'w', encoding='utf-8') as f:
                        f.write(content)
                    print(f"  [YAML] application.yml")
                    recovered_yaml += 1
                    continue
            if 'jwt:' in content and ('secret:' in content or 'expiration:' in content):
                fp = f"{base}/backend/src/main/resources/application-dev.yml"
                if not os.path.exists(fp):
                    os.makedirs(os.path.dirname(fp), exist_ok=True)
                    with open(fp, 'w', encoding='utf-8') as f:
                        f.write(content)
                    print(f"  [YAML] application-dev.yml")
                    recovered_yaml += 1
                    continue

    # === GITHUB ACTIONS CI ===
    if 'on:' in content and ('push:' in content or 'pull_request:' in content) and 'jobs:' in content:
        fp = f"{base}/.github/workflows/ci.yml"
        if not os.path.exists(fp):
            os.makedirs(os.path.dirname(fp), exist_ok=True)
            with open(fp, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  [YAML] .github/workflows/ci.yml")
            recovered_yaml += 1
            continue

    # === POM.XML ===
    if '<project' in content and '</project>' in content and '<groupId>com.renewable.ai</groupId>' in content:
        fp = f"{base}/backend/pom.xml"
        if not os.path.exists(fp):
            os.makedirs(os.path.dirname(fp), exist_ok=True)
            with open(fp, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  [XML]  backend/pom.xml")
            recovered_xml += 1
            continue

    # === PACKAGE.JSON ===
    if '"name":' in content and ('"dependencies":' in content or '"devDependencies":' in content):
        if '"vue"' in content or '"axios"' in content:
            fp = f"{base}/frontend/package.json"
            if not os.path.exists(fp):
                os.makedirs(os.path.dirname(fp), exist_ok=True)
                with open(fp, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"  [JSON] frontend/package.json")
                recovered_other += 1
                continue

    # === VITE CONFIG ===
    if 'export default' in content and ('defineConfig' in content or 'vite' in content.lower()):
        fp = f"{base}/frontend/vite.config.js"
        if not os.path.exists(fp):
            os.makedirs(os.path.dirname(fp), exist_ok=True)
            with open(fp, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  [JS]   frontend/vite.config.js")
            recovered_js += 1
            continue

    # === VITEST CONFIG ===
    if 'vitest/config' in content or ("import { defineConfig }" in content and 'environment:' in content):
        fp = f"{base}/frontend/vitest.config.mjs"
        if not os.path.exists(fp):
            os.makedirs(os.path.dirname(fp), exist_ok=True)
            with open(fp, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  [JS]   frontend/vitest.config.mjs")
            recovered_js += 1
            continue

    # === MAIN.JS ===
    if "createApp" in content and "mount" in content:
        fp = f"{base}/frontend/src/main.js"
        if not os.path.exists(fp):
            os.makedirs(os.path.dirname(fp), exist_ok=True)
            with open(fp, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  [JS]   frontend/src/main.js")
            recovered_js += 1
            continue

    # === APP.VUE ===
    if content.strip().startswith('<template>') and "<router-view" in content:
        fp = f"{base}/frontend/src/App.vue"
        if not os.path.exists(fp):
            os.makedirs(os.path.dirname(fp), exist_ok=True)
            with open(fp, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  [VUE]  App.vue")
            recovered_vue += 1
            continue

    # === TEST SETUP ===
    if 'vi.mock' in content or 'localStorage' in content:
        fp = f"{base}/frontend/src/test/setup.js"
        if not os.path.exists(fp):
            os.makedirs(os.path.dirname(fp), exist_ok=True)
            with open(fp, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  [JS]   frontend/src/test/setup.js")
            recovered_js += 1
            continue

    # === MARKDOWN ===
    if re.match(r'^#\s+', content) and ('##' in content or '```' in content):
        if '# Smart Waste' in content or '# 智慧垃圾分类' in content:
            if 'Tracker' in content or '任务' in content:
                fp = f"{base}/docs/plans/2026-05-03-smart-waste-remediation-tracker.md"
            elif 'Plan' in content or '计划' in content:
                fp = f"{base}/docs/plans/2026-05-03-smart-waste-remediation-plan.md"
            elif 'Design' in content or '设计' in content:
                fp = f"{base}/docs/plans/2026-05-03-smart-waste-remediation-design.md"
            elif 'README' in content:
                fp = f"{base}/README.md"
            else:
                fp = f"{base}/docs/plans/recovered_doc_{i}.md"
            if not os.path.exists(f"{base}/{fp}"):
                os.makedirs(os.path.dirname(f"{base}/{fp}"), exist_ok=True)
                with open(f"{base}/{fp}", 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"  [MD]   {os.path.basename(fp)}")
                recovered_md += 1

    # === PLAYWRIGHT CONFIG ===
    if 'playwright' in content.lower() and ('testMatch' in content or 'webServer' in content or 'defineConfig' in content):
        fp = f"{base}/frontend/tests/e2e/playwright.config.js"
        if not os.path.exists(fp):
            os.makedirs(os.path.dirname(fp), exist_ok=True)
            with open(fp, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"  [JS]   playwright.config.js")
            recovered_js += 1
            continue

print(f"\n=== Recovery Summary ===")
print(f"  Java:    {recovered_java}")
print(f"  Vue:     {recovered_vue}")
print(f"  JS:      {recovered_js}")
print(f"  YAML:    {recovered_yaml}")
print(f"  XML:     {recovered_xml}")
print(f"  Other:   {recovered_other}")
print(f"  Markdown:{recovered_md}")
print(f"  Skipped: {skipped}")
print(f"  Total:  {recovered_java+recovered_vue+recovered_js+recovered_yaml+recovered_xml+recovered_other+recovered_md}")
