# react-native-sunmi-eid

sunmi eid service for react-native app

## Installation

```sh
npm install react-native-sunmi-eid
```

## Usage

```js
import SunmiEid from "react-native-sunmi-eid";

// 初始化，建议全局只初始化一次
SunmiEid.init({
  appKey: '',
  appId: ''
}).then(() => {
  ...
})

SunmiEid.startCheckCard((status, code, msg, info) => {
  if (status === 'SUCCESS') {
    // 解析成功
  }
  if (status === 'READY') {
    // 准备完成，可以开始刷卡
  }
  if (status === 'PENDING') {
    // 刷卡中
  }
  if (status === 'DONE') {
    // 刷卡完成，等待解析
  }
  if (status === 'FAILED') {
    // 刷卡/解析失败
  }
}, (code, msg) => {
  // 刷卡/解析过程中各种异常
})
// 手动关闭读卡
SunmiEid.stopCheckCard()

```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
