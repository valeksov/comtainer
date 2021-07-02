import { Content } from 'components/common/Content';
import { XlsxConverter } from 'components/main/XlsxConverter';
import React, { PropsWithChildren } from 'react';
import styles from './Home.module.scss';

type HomeProps = {};

const HomeComponent = (props): HomeProps => {
    return (
        <Content>
            <XlsxConverter />
        </Content>
    );
};

const Home = HomeComponent;
export { Home };
