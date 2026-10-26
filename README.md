<p align="center">
  <img src="docs/images/icon.png" width="88" alt="8bercueLa 应用图标">
</p>

<h1 align="center">8bercueLa</h1>

<p align="center">开源 Android eSIM 管理器，在自己的手机上管理卡片与配置文件。</p>

<p align="center">
  <a href="https://github.com/woworz/8bercueLa">当前仓库</a> ·
  <a href="https://github.com/woworz/8bercueLa/releases">本仓库发布 APK</a> ·
  <a href="https://github.com/sidnvy/8bercue/releases">上游已发布 APK</a> ·
  <a href="BUILDING.md">构建说明</a>
</p>

8bercueLa 用于管理兼容 eUICC 上的 eSIM 配置文件，也支持 5ber 等可插拔 eUICC 卡片，无需注册或登录 5ber 账户。

本仓库基于 [sidnvy/8bercue](https://github.com/sidnvy/8bercue)，在原有电脑 ADB 连接之外新增了 **Shizuku 授权**。本文说明当前源码的功能；上游已发布 APK 是否包含相应功能，请以其发布说明为准。

<p align="center">
  <img src="docs/images/profiles.png" width="260" alt="eSIM 配置文件列表与状态">
  &nbsp;&nbsp;
  <img src="docs/images/profile-details.png" width="260" alt="eSIM 详情与管理操作">
</p>

## 功能

- 查看 eSIM 配置文件、启用状态和卡片信息。
- 重命名、启用、停用和删除配置文件。
- 扫描二维码、导入二维码图片或手动输入激活码，添加 eSIM。
- 通过本机 Shizuku 或电脑 ADB 授权，连接卡片管理服务。
- 自动检测卡片访问通道，查看兼容性结果，并在存在多张可读卡片时手动选择。
- 提供操作记录与只读恢复流程，核实中断或结果不明确的操作。
- 支持简体中文和英文，可跟随系统语言或在应用内切换。
- 保存本地诊断 ZIP，不包含问题反馈或崩溃上报功能。

安装和删除需要确认，已启用的配置文件需要先停用才能删除。具体可用操作取决于卡片、访问通道和当前恢复状态。

## 使用条件

- **ARM64 架构、Android 9 或更高版本**。
- 兼容的 eUICC，以及可用且具备所需系统权限的 Secure Element HAL、Telephony 或 OMAPI 通道。
- 在手机的主用户中运行应用并完成授权。

应用能够安装、ADB 或 Shizuku 能够授权，并不代表一定能够访问卡片。当前源码会优先检查 Secure Element HAL，再尝试 Telephony 和 OMAPI；实际结果可在“设置 → 兼容性检查”中查看。HIDL 通道在此版本中仅支持读取。

设备差异与已知限制见[设备兼容性说明](docs/COMPATIBILITY.md)，首次使用新手机可参考[新设备检查流程](docs/NEW_DEVICE_CHECK.md)。

## 连接方式

两种方式启动相同的管理服务，连接后的卡片操作、兼容性检查和恢复保护保持一致。任选一种即可。

### 通过 Shizuku 连接

1. 安装 [Shizuku](https://shizuku.rikka.app/download/)，按照其[官方指南](https://shizuku.rikka.app/guide/setup/)通过无线调试或 ADB 启动。
2. 打开 8bercueLa，在首页点击“连接卡片 → 通过 Shizuku 连接”，也可从设置中进入。
3. 在 Shizuku 授权弹窗中允许 8bercueLa，等待本地服务连接和卡片检查完成。
4. 如果之前拒绝了授权，请在 Shizuku 的“已授权应用”中允许 8bercueLa，再返回应用重试。

**当前实现要求 Shizuku API v12 或更高版本，并以 Shell（UID 2000）运行，暂不支持 Root 启动模式或 Sui。**

Shizuku 在这里用于启动现有管理服务。连接建立后，停止 Shizuku 或撤销其授权不会终止已连接的管理服务，也不会中断正在进行的卡片操作。详细机制与验证范围见 [Shizuku 接入说明](docs/SHIZUKU.md)。

### 通过电脑 ADB 连接

1. 安装应用，在手机上启用 USB 调试，并授权可信电脑。
2. 打开 8bercueLa，选择“通过电脑连接”。
3. 点击“复制 ADB 命令”，在页面显示的两分钟有效期内，在电脑终端执行该命令。
4. 等待终端确认连接成功，再返回应用检查卡片访问状态。

命令会自动查找当前安装的 APK，安装路径或应用更新后无需手动修改。如果倒计时结束，点击“重新连接”，再执行同一条命令。无需额外下载 Python 脚本或安装辅助应用。

连接了多台设备时，请将命令中的 `adb shell` 改为 `adb -s SERIAL shell`，其中 `SERIAL` 为目标设备的序列号。

### 连接后

先确认“兼容性检查”的结果和最新配置文件列表，再进行更改。如果发现多张可读卡片，请通过“选择卡片”确认目标 eUICC。

重启手机、更新应用或管理服务终止后，需要重新连接。连接本身不会安装、删除或切换任何配置文件。

## 操作记录与恢复

应用会核验操作前后的卡片状态。遇到服务断开、操作中断或结果不明确的情况时，待处理操作不会自动重试。

只读恢复会保留原始记录，在核实当前卡片与恢复状态前暂停更改。请按应用提示检查，避免重复执行结果未知的安装、删除或切换操作。具体行为见[只读恢复说明](docs/READ_ONLY_RECOVERY.md)。

当前项目仍处于 **Beta** 阶段。授权成功不等于设备兼容性或全部管理操作已经验证；Shizuku 接入仍需要在实际设备上验证。

## 构建与贡献

当前应用源码版本为 `0.19.1-beta.4`，`versionCode` 为 `49`。构建工具链、原生组件、依赖校验和签名方法见 [BUILDING.md](BUILDING.md)。自行构建的 APK 需要使用自己的签名；覆盖安装已有应用时，Android 要求签名兼容。

欢迎提交代码改进。反馈问题时，请说明应用版本、Android 版本、设备型号、连接方式及兼容性检查结果，不要公开激活码、完整 EID、ICCID 或其他敏感信息。

## 许可证与上游

原项目作者：sid · [上游仓库](https://github.com/sidnvy/8bercue) · [上游反馈](https://github.com/sidnvy/8bercue/issues) · [support@sidnvy.com](mailto:support@sidnvy.com)

原有代码与本仓库修改遵循项目的 [AGPL-3.0-only 许可证](COPYING)。第三方组件保留各自许可证，详见[许可证说明](LICENSING.md)。隐私说明见 [PRIVACY.txt](PRIVACY.txt)，应用内提供简体中文译文。

8bercueLa 是独立项目，与 5ber 无隶属关系。
