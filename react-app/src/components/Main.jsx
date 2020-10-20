import React from 'react';
import { Switch, Route } from 'react-router-dom';

import DemoApp from "./DemoApp";
import HistoryView from "./HistoryView";

const Main = () => {
    return (
        <Switch> {/* The Switch decides which component to show based on the current URL.*/}
            <Route exact path='/' component={DemoApp}></Route>
            <Route exact path='/:history' component={HistoryView}></Route>
        </Switch>
    );
}

export default Main;