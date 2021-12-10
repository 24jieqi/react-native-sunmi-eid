import * as React from 'react';

import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import SunmiEid from 'react-native-sunmi-eid';

export default function App() {
  function handleScan() {
    SunmiEid.startCheckCard();
  }
  React.useEffect(() => {
    // appId & appKey请从商米伙伴后台获取
    SunmiEid.init({
      appId: 'xxx',
      appKey: 'xxx',
    })
      .then(() => {
        console.log('初始化成功！！！！');
      })
      .catch((err) => {
        console.log('err12', err);
      });
    const removeEvent = SunmiEid.listen(
      (payload) => {
        console.log('stateChange', payload.status);
      },
      (error) => {
        console.log(error);
      }
    );
    return () => {
      removeEvent();
    };
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
