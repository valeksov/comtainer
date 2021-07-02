import React from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import 'react-toastify/dist/ReactToastify.css';
import { Home } from 'components/pages/Home';

function App() {
    return (
        <BrowserRouter>
            <Switch>
                <Route path="/" exact component={Home} />
            </Switch>
        </BrowserRouter>
    );
}

export default App;
