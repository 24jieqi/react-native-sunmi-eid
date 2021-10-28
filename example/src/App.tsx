import * as React from 'react';

import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import SunmiEid from 'react-native-sunmi-eid';

export default function App() {
  function onStatusChange(status: any, code: any, msg: any, info: any) {
    console.log(status, code, msg, info);
  }
  function onError(code: any, msg: any) {
    console.log(code, msg);
  }
  function handleScan() {
    SunmiEid.startCheckCard(onStatusChange, onError);
  }
  React.useEffect(() => {
    SunmiEid.init({
      appId: 'cf67b48080b84cf694fb16fabd5098e3',
      appKey: '8da4d40e24fb4c149905dc74784ac09a',
    })
      .then(() => {
        console.log('初始化成功！！！！');
      })
      .catch((err) => {
        console.log('err12', err);
      });
  }, []);

  return (
    <View style={styles.container}>
      <Text>Result: 测试！</Text>
      <TouchableOpacity onPress={handleScan}>
        <Text>开始扫描</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
