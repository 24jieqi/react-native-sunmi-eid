# react-native-sunmi-eid

sunmi eid service for react-native app

商品身份证云识别 react-native 实现

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
// 手动开启读卡
SunmiEid.startCheckCard()
// 手动关闭读卡
SunmiEid.stopCheckCard()

// 监听读卡过程中状态变更
const removeEvent = SunmiEid.listen(
  (payload) => {
    console.log('stateChange', payload.status);
  },
  (error) => {
    console.log('error', error);
  }
);

```

> 更详细的使用方式请查看 example

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
