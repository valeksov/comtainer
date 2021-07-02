import App from 'components/App';
import React from 'react';
import { StoreProvider } from 'store/StoreProvider';

const AppRoot = () => {
    return (
        <StoreProvider>
            <App />
        </StoreProvider>
    );
};

export default AppRoot;
