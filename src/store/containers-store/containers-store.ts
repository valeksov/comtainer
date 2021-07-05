import { ContainersApi } from 'api/ContainersAPI';
import { makeAutoObservable } from 'mobx';
import { RootStore } from '../root-store';

export class ContainersStore {
    rootStore: RootStore;
    containersApi: ContainersApi;

    constructor(rootStore: RootStore) {
        const {
            generalStore: { containersApi },
        } = rootStore;

        this.containersApi = containersApi;
        this.rootStore = rootStore;
        makeAutoObservable(this);
    }

    getLoadingPlan = async jsonFile => {
        const response = await this.containersApi.fetchLoadPlan(jsonFile);
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
