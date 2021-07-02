import { makeAutoObservable } from 'mobx';
import { ToastContainer, toast } from 'react-toastify';
import { RootStore } from '../root-store';
import { commonToastOptions } from './general-store.utils';

export class GeneralStore {
    rootStore: RootStore;

    constructor(rootStore: RootStore) {
        this.rootStore = rootStore;
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
