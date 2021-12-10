import { NativeModules, NativeEventEmitter } from 'react-native';

export type ReadyStatus =
  | 'READY'
  | 'PENDING'
  | 'DONE'
  | 'FAILED'
  | 'SUCCESS'
  | 'PARSE_FAILED';
interface IConfig {
  appId: string;
  appKey: string;
}
export interface IEidCardInfo {
  dn: string;
  name: string;
  sex: string;
  nation: string;
  birthDate: string;
  address: string;
  idnum: string;
  signingOrganization: string;
  beginTime: string;
  endTime: string;
  picture: string;
}
interface SunmiEidType {
  init: (config: IConfig) => Promise<null>;
  stopCheckCard: () => void;
  startCheckCard: () => void;
  listen: (
    onStateChange: (payload: SunmiEidEventMessageType) => void,
    onError: (payload: any) => void
  ) => () => void;
}

export interface SunmiEidEventMessageType {
  status: ReadyStatus;
  code: number;
  message: string;
  result: any;
}

const SunmiEid = NativeModules.SunmiEid as SunmiEidType;

SunmiEid.listen = (onStateChange, onError) => {
  const eventEmitter = new NativeEventEmitter(NativeModules.PrinterSunmi);
  const stateChangeEvent = eventEmitter.addListener(
    'onStateChange',
    (payload) => {
      onStateChange(payload);
    }
  );
  const errorEvent = eventEmitter.addListener('onError', (payload) => {
    onError(payload);
  });
  return () => {
    stateChangeEvent.remove();
    errorEvent.remove();
  };
};

export default SunmiEid;
