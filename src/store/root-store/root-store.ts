import { ContainersStore } from 'store/containers-store';
import { GeneralStore } from '../general-store';

export class RootStore {
    generalStore = new GeneralStore(this);
    containersStore = new ContainersStore(this);
}
