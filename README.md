# ScriptPen 剧本笔记 — 自动构建与安装指南

一个原生 **Kotlin + Jetpack Compose** 的安卓剧本笔记 App（完整源码）。
本项目已内置 **GitHub Actions** 工作流：把代码推到 GitHub 后，云端自动构建出 APK，**你本机无需安装 Android Studio / Android SDK**。

## 你需要准备

| 项目 | 说明 |
|---|---|
| GitHub 账号 | 已具备 |
| 一台能运行 `git` 的电脑 | 仅用于把代码推到 GitHub（**不**需要装 Android SDK） |
| 安卓手机 | Android 7.0+（minSdk 24） |

> 注：本项目的云端开发环境无法直连 GitHub，所以"推送代码"这一步需要你在自己的电脑上完成；之后的编译/出包全部交给 GitHub 云端。

## 三步拿到 APK

### 1. 在 GitHub 新建空仓库
登录 GitHub → **New repository** → 名字随意（如 `ScriptPen`）→ Public 或 Private 均可 → **不要**勾选 "Add a README file" → Create repository。

### 2. 把代码推上去
把 `ScriptPen.zip` 下载到电脑、解压后，在文件夹里执行：

```bash
cd ScriptPen
git init
git add .
git commit -m "init ScriptPen"
git branch -M main
git remote add origin https://github.com/<你的用户名>/<仓库名>.git
git push -u origin main
```

### 3. 触发云端构建并下载 APK
1. 打开仓库 → **Actions** 标签页 → 左侧 `Build APK` → 点 **Run workflow** → **Run**。
   （之后每次 `git push` 也会自动触发。）
2. 等待 3–8 分钟，状态变绿 ✅ → 点进该次运行 → 右侧 **Artifacts** 区下载 `app-debug-apk`（zip）。
3. 解压得到 `app-debug.apk`。

### 4. 安装到手机
- 把 apk 传到手机（微信 / 数据线 / 网盘均可），点开安装。
- 若提示"未知来源"，按系统提示允许本次安装即可（debug 包无需额外签名）。

## 关于签名

- 首版产出的是 **debug APK**，由 CI 自带 debug keystore 签名，可直接安装测试，**无需你提供任何密钥信息**。
- 若日后要正式发布（如上架 Google Play），再生成自己的 keystore 并存入仓库 **Secrets**，工作流稍作调整即可。届时告诉我即可。

## 备选：本机用 Android Studio 打开

如果你想本地改代码 / 编译：
1. Android Studio 打开本项目目录（`ScriptPen/`）。
2. Gradle 8.9 与依赖已配置腾讯云 / 阿里云镜像，国内下载快。
3. 本机需装有 **Android SDK（API 34）+ JDK 17**。
4. 点 ▶ Run 装到手机，或 `Build → Build APK(s)` 生成 `app-debug.apk`。

## 项目结构要点

| 路径 | 作用 |
|---|---|
| `app/build.gradle.kts` | compileSdk/targetSdk 34；Room / Compose / Navigation / DataStore 依赖 |
| `.github/workflows/build-apk.yml` | CI 构建工作流（装 SDK → 构建 → 上传 APK） |
| `app/src/main/java/com/scriptpen/app/data/` | Room 数据层 |
| `app/src/main/java/com/scriptpen/app/viewmodel/` | 列表 / 编辑器 ViewModel |
| `app/src/main/java/com/scriptpen/app/ui/` | 主题、屏幕、组件（Compose） |
| `app/src/main/java/com/scriptpen/app/util/` | 字数统计 / 自动排版 / fdx 解析 / 文件导入 |

## 已实现功能

- 多剧本项目管理（新建 / 重命名 / 删除）
- 剧本编辑器：文档级背景色（6 种预设）+ 字号（12–28sp）调节
- 搜索 / 替换 / 批量替换（上 / 下一个定位 + 高亮）
- 导入 txt / md / fdx（Final Draft XML 解析）
- 轻量自动排版（压缩空行、清理行尾、场景标题识别）
- 实时字数统计（字符 / 中文 / 英文词 / 行 / 段 / 场景数）
- Material 3 深色简约 UI

## 常见问题

- **构建卡在下载/超时**：CI 默认从腾讯云镜像拉 Gradle；Runner 本身网络正常可访问 Google 源。偶发卡顿重试一次即可。
- **本地 `gradlew` 无执行权限**：先 `chmod +x gradlew` 再运行。
- **想换应用名 / 包名**：`app/src/main/res/values/strings.xml` 改 `app_name`；包名在 `app/build.gradle.kts` 的 `namespace` / `applicationId`。
