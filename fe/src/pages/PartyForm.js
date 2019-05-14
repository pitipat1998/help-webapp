import React, {Component} from "react";
import axios from "../utils/axiosConfig"
import {withRouter} from "react-router-dom";
import {Button, Form, FormLabel, FormGroup, FormControl} from "react-bootstrap";

class PartyForm extends Component {

    state = {
        name : ""
    };

    componentWillMount() {
        axios.get("/api/authentication")
            .then( res => {
                console.log(res);
                if (!res.data.status) {
                    this.props.history.push("/sign-in")
                }
                else {
                    this.props.onAuth();
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
        const url = `/api/parties`;
        const data = {...this.state};
        axios.post(url, data)
            .then( res => {
                this.clearForm();
                console.log(res);
                if(res.data.status.status){
                    this.props.onResponse({success:true, successMsg: res.data.status.msg});
                    this.props.history.push('/parties')
                }
                else{
                    this.props.onResponse({err:true, errMsg: res.data.status.msg});
                }
            })
            .catch( err => {
                console.log(err) ;
            })
    };

    validateForm = () => {
        const {name} = this.state;
        return name.length > 0;
    };

    clearForm = () => {
        this.setState({name: ""});
    };

    render(){
        const {name} = this.state;
        return (
            <div>
                <h1>Create Party</h1>
                <Form onSubmit={this.handleSubmit}>
                    <FormGroup>
                        <FormLabel>Party Name</FormLabel>
                        <FormControl type="text" name="name" placeholder="Enter Your Party Name" value={name} onChange={this.handleChange}/>
                    </FormGroup>
                    <Button block disabled={!this.validateForm()}
                            type="submit">
                        Create
                    </Button>
                </Form>
            </div>
        );
    }
}

export default withRouter(PartyForm)
