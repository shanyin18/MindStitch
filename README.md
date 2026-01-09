# MindStitch (思维缝合)

MindStitch 是一款基于 Android 的灵感捕捉与管理应用，旨在帮助用户像“缝合”思维碎片一样，将零散的想法整理成体系。

## 功能特性 (Features)

*   **灵感捕捉 (Idea Capture)**: 
    *   快速记录文字和图片。
    *   支持 Markdown 语法渲染。
    *   图文混排编辑体验。
*   **数据整理 (Organization)**:
    *   支持文件夹管理。
    *   灵感“热度”与标签系统。
*   **数据同步 (Sync)**:
    *   支持 WebDAV 云端备份与恢复（包括图片资源）。
*   **本地存储 (Storage)**:
    *   使用 Room 数据库进行本地持久化。

## 技术栈 (Tech Stack)

*   **语言**: Kotlin
*   **UI 框架**: Jetpack Compose (Material 3)
*   **架构**: MVVM
*   **数据库**: Room Database
*   **依赖注入**: Hilt (如有) / Manual DI
*   **构建工具**: Gradle (Kotlin DSL)

## 快速开始 (Getting Started)

1.  克隆仓库。
2.  在 Android Studio 中打开 `MindStitchApp` 目录。
3.  同步 Gradle 构建。
4.  运行到 Android 设备或模拟器。

## 注意事项

*   请确保 `local.properties` 中配置了正确的 `sdk.dir`。
*   WebDAV 功能需要在设置中配置个人的 WebDAV 服务器信息。
