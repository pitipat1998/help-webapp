import React, { Component } from 'react';
import './App.css';

import Routes from "./components/Routes";
import {AuthNav, NotAuthNav} from "./components/NavBars"
import {Alerts} from "./components/Alerts";

class App extends Component {
    state = {
        isAuth: false,
        err : false,
        errMsg : "",
        success : false,
        successMsg : "",
    };

    handleAuth = () => {
        this.setState({isAuth : true})
    };

    handleResponse = (obj) => {
        this.setState({...obj})
    };

    handleHide = () => {
        this.setState({err:false, errMsg:"", success:false, successMsg:""})
    };

    render = () => {
        const navOption = (this.state.isAuth) ? <AuthNav/> : <NotAuthNav/>;
        return (
            <div className="App">
                {navOption}
                <Alerts show={this.state.err} status="danger" msg={this.state.errMsg} heading="Oh snap! You got an error!" handleHide={this.handleHide}/>
                <Alerts show={this.state.success} status="success" msg={this.state.successMsg} heading="Congrats!!" handleHide={this.handleHide}/>
                <Routes handleAuth={this.handleAuth} handleResponse={this.handleResponse}/>
            </div>
        )
    };
}

export default App;
