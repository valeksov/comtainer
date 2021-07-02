import { ToastOptions } from 'react-toastify/dist/types';
import { COLORS } from '../../constants';

export const commonToastOptions: ToastOptions = {
    position: 'top-center',
    autoClose: 5000,
    hideProgressBar: false,
    closeOnClick: true,
    pauseOnHover: true,
    draggable: true,
    progress: undefined,
};
