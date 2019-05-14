import React from "react";
import { Route, Switch } from "react-router-dom";
import SignIn from '../pages/SignIn';
import SignUp from '../pages/SignUp';
import SignOut from '../pages/SignOut';
import UserProfile from '../pages/UserProfile';
import Restaurants from '../pages/Restaurants';
import Restaurant from '../pages/Restaurant';
import RestaurantForm from '../pages/RestaurantForm'
import Parties from '../pages/Parties'
import PartyForm from '../pages/PartyForm'
import Party from '../pages/Party'
import Statistics from '../pages/Statistics'

export default (props) =>
    <Switch>
        <Route exact path="/" render={() => <UserProfile onAuth={props.handleAuth}/>} />
        <Route path="/sign-in" render={() => <SignIn onResponse={props.handleResponse}/>}/>
        <Route path="/sign-up" render={() => <SignUp onResponse={props.handleResponse}/>}/>
        <Route path="/sign-out" render={() => <SignOut onResponse={props.handleResponse}/>}/>

        <Route path="/user-profile" render={() => <UserProfile onAuth={props.handleAuth}/>}/>

        <Route path="/restaurants/:id" render={() => <Restaurant onAuth={props.handleAuth} onResponse={props.handleResponse}/>}/>
        <Route exact path="/restaurants" render={() => <Restaurants onAuth={props.handleAuth} onResponse={props.handleResponse}/>}/>
        <Route path="/restaurant-form" render={() => <RestaurantForm onAuth={props.handleAuth} onResponse={props.handleResponse}/>}/>

        <Route path="/parties/:id" render={() => <Party onAuth={props.handleAuth} onResponse={props.handleResponse}/>}/>
        <Route exact path="/parties" render={() => <Parties onAuth={props.handleAuth} onResponse={props.handleResponse}/>}/>
        <Route path="/party-form" render={() => <PartyForm onAuth={props.handleAuth} onResponse={props.handleResponse}/>}/>

        <Route path="/statistics" render={() => <Statistics onAuth={props.handleAuth}/>} />
    </Switch>;