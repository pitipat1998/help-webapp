import React, {Component} from "react";
import axios from "../utils/axiosConfig";
import {withRouter} from "react-router-dom";
import { Button, Form, FormControl, FormGroup, FormLabel } from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.css';
import '../styles/SignIn.css'

class SignIn extends Component {

    state = {
        username : "",
        password : "",
    };

    componentDidMount() {
        axios.get("/api/authentication")
            .then( res => {
                console.log(res);
                if (res.data.status) {
                    this.props.history.push("/user-profile")
                }
            })
            .catch( err =>
                console.log(err)
            );
    }

    handleChange = (e) => {
        this.setState({
            [`${e.target.name}`]: e.target.value
        });
    };

    handleSubmit = (e) => {
       e.preventDefault();
       const url = `/api/sign-in`;
       const data = {...this.state};
       axios.post(url, data)
           .then( res => {
                this.clearForm();
                console.log(res);
                if(res.data.status){
                    this.props.onResponse({success:true, successMsg: res.data.msg});
                    this.props.history.push('/user-profile')
                }
                else{
                    this.props.onResponse({err:true, errMsg: res.data.msg});
                }
            })
           .catch( err => {
                console.log(err) ;
           })
   };

    clearForm = () => {
        this.setState({username: "", password: ""});
    };


    validateForm = () => {
        const {username, password} = this.state;
        return username.length > 0 && password.length > 0;
    };

    render(){
        const {username, password} = this.state;
        return (
            <div className="Login">
                <h1>Sign In</h1>
                <Form onSubmit={this.handleSubmit}>
                    <FormGroup controlId="username">
                        <FormLabel>Username</FormLabel>
                        <FormControl autoFocus type="text" value={username} onChange={this.handleChange} name="username" placeholder="Enter Username"/>
                    </FormGroup>
                    <FormGroup controlId="password">
                        <FormLabel>Password</FormLabel>
                        <FormControl value={password} onChange={this.handleChange} type="password" name="password" placeholder="Enter Password"/>
                    </FormGroup>
                    <Button block disabled={!this.validateForm()}
                         type="submit">
                        Sign In
                    </Button>
                </Form>
            </div>
        );
    }
}

export default withRouter(SignIn)
