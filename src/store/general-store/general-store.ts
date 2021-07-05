import { ApiUtilArguments } from 'api/APIUtil';
import { ContainersApi } from 'api/ContainersAPI';
import { makeAutoObservable } from 'mobx';
import { ToastContainer, toast } from 'react-toastify';
import { RootStore } from '../root-store';
import { commonToastOptions } from './general-store.utils';

export class GeneralStore {
    rootStore: RootStore;
    containersApi: ContainersApi;

    constructor(rootStore: RootStore) {
        this.rootStore = rootStore;

        const apiHandlers: ApiUtilArguments = {
            onError: this.handleApiError,
            onFetchStart: this.setLoadingOn,
            onFetchEnd: this.setLoadingOff,
        };

        this.containersApi = new ContainersApi(apiHandlers);

        makeAutoObservable(this);
    }

    // NAVIGATION
    navigation = null;

    setNavigationRef = ref => {
        this.navigation = ref;
    };

    // LOADING FLAG
    isLoading: boolean = false;

    setIsLoading = (value: boolean): void => {
        this.isLoading = value;
    };

    setLoadingOn = (): void => {
        this.setIsLoading(true);
    };

    setLoadingOff = (): void => {
        this.setIsLoading(false);
    };

    // HANDLE ERRORS
    handleApiError = (error: any): void => {
        console.log({ error });
    };

    // METHODS
    clearStores = (): void => {
        // TODO vasko - implement later if needed.
    };

    showInfoMessage = (message: string): void => {
        toast.info(message, commonToastOptions);
    };

    showErrorMessage = (message: string): void => {
        toast.error(message || 'Something went wrong', commonToastOptions);
    };

    showSuccessMessage = (message: string): void => {
        toast.success(message, commonToastOptions);
    };
}
