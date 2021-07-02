import React, { PropsWithChildren, createContext, useContext, ReactNode } from 'react';
import { configure } from 'mobx';
import { RootStore } from './root-store';

configure({
    enforceActions: 'never',
});

type StoreProviderProps = {
    children: ReactNode;
};

const rootStore = new RootStore();
const StoreContext = createContext<RootStore>(rootStore);

export const StoreProvider = ({ children }: StoreProviderProps) => {
    return <StoreContext.Provider value={rootStore}>{children}</StoreContext.Provider>;
};

export const useRootStore = () => useContext(StoreContext);
