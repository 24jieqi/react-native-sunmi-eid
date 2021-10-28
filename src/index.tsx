import { NativeModules } from 'react-native';

export type ReadyStatus = 'READY' | 'PENDING' | 'DONE' | 'FAILED' | 'SUCCESS';

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
  startCheckCard: (
    statusChangeCallback: (
      status: ReadyStatus,
      code: number,
      msg: number,
      info?: IEidCardInfo
    ) => void,
    errorCallback: (code: number, msg: string) => void
  ) => void;
}

const SunmiEid = NativeModules.SunmiEid as SunmiEidType;

export default SunmiEid;
