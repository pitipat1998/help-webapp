import React, {Component} from "react";
import axios from "../utils/axiosConfig"
import {withRouter} from "react-router-dom";
import {Button, Form, FormLabel, FormGroup, FormControl} from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.css';
import '../styles/SignUp.css'

class SignUp extends Component {

    state = {
        username : "",
        password : "",
        email : "",
        address : "",
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
        const url = `/api/sign-up`;
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

    validateForm = () => {
        const {username, password, email, address} = this.state;
        return username.length > 0 && password.length > 0 && email.length > 0 && address.length > 0;
    };

    clearForm = () => {
        this.setState({username: "", password: "", email: "", address: ""});
    };

    render(){
        const {username, password, email, address} = this.state;
        return (
            <div>
                <h1>Sign up</h1>
                <Form onSubmit={this.handleSubmit}>
                    <FormGroup>
                        <FormLabel>Username</FormLabel>
                        <FormControl type="text" name="username" placeholder="Enter Your Username" value={username} onChange={this.handleChange}/>
                    </FormGroup>
                    <FormGroup>
                        <FormLabel>Password</FormLabel>
                        <FormControl type="password" name="password" placeholder="Enter Your Password" value={password} onChange={this.handleChange}/>
                    </FormGroup>
                    <FormGroup>
                        <FormLabel>Email</FormLabel>
                        <FormControl type="email" name="email" placeholder="Enter Your Email" value={email} onChange={this.handleChange}/>
                    </FormGroup>
                    <FormGroup>
                        <FormLabel>Address</FormLabel>
                        <FormControl type="textarea" name="address" placeholder="Enter Your address" value={address} onChange={this.handleChange}/>
                    </FormGroup>
                    <Button block disabled={!this.validateForm()}
                        type="submit">
                        Sign Up
                    </Button>
                </Form>
            </div>
        );
    }
}



export default withRouter(SignUp)