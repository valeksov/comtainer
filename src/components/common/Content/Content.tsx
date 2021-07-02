import React, { PropsWithChildren, ReactNode } from 'react';
import { ToastContainer } from 'react-toastify';
import styles from './Content.module.scss';

type ContentProps = {
    children: ReactNode;
};

const ContentComponent: React.FC = ({ children }: ContentProps) => {
    return (
        <div className={styles.container}>
            {children}
            <ToastContainer />
        </div>
    );
};

const Content = ContentComponent;
export { Content };
