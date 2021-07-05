import { ContainersApi } from 'api/ContainersAPI';
import { makeAutoObservable } from 'mobx';
import { ConvertedXlsDto } from 'services/generate-json/generate-json.types';
import { RootStore } from '../root-store';

export class ContainersStore {
    rootStore: RootStore;
    containersApi: ContainersApi;

    constructor(rootStore: RootStore) {
        const {
            generalStore: { containersApi },
        } = rootStore;

        this.rootStore = rootStore;
        this.containersApi = containersApi;

        makeAutoObservable(this);
    }

    getLoadPlan = async (jsonObject: ConvertedXlsDto) => {
        const response = await this.containersApi.getLoadPlan(jsonObject);
        if (!response) return;
        return response;
    };

    // Leave code block for future reference.
    // public login = async (payload: LoginCandidate): Promise<void> => {
    //     const authPayload = await this.userApi.loginUser(payload);

    //     if (!authPayload) return;

    //     await this.authenticateUser(authPayload);
    //     await this.rootStore.ordersStore.loadRiskLevelsAndOrderTypes();
    //     this.rootStore.generalStore.resetAndNavigateTo(APP_SCREENS.groups);
    //     userService.setAuthPayload(authPayload);
    // };
}
