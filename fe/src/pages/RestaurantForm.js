import React, {Component} from "react";
import axios from "../utils/axiosConfig"
import {withRouter} from "react-router-dom";
import {Button, Form, FormLabel, FormGroup, FormControl, ButtonGroup} from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.css';
import '../styles/SignUp.css'

const Cuisines = (props) => {
    const cuisines = props.cuisines.map(
        cuisine => <Button key={cuisine.id} active={cuisine.id === props.selectedCuisine} onClick={props.handleClick} value={cuisine.id}>{cuisine.cuisine}</Button>
    );
    return cuisines;
};

class RestaurantForm extends Component {

    state = {
        name : "",
        cuisineID: null,
        cuisines: []
    };

    handleChange = (e) => {
        this.setState({
            [`${e.target.name}`]: e.target.value
        });
    };

    handleClick = (e) => {
        this.setState({
            cuisineID: parseInt(e.target.value)
        })
    };

    componentWillMount() {
        axios.get("/api/cuisines")
            .then( res => {
                console.log(res);
                if (res.data.payload == null) {
                    this.props.history.push("/sign-in")
                }
                else {
                    this.props.onAuth();
                    this.setState({
                        cuisines: res.data.payload
                    });
                }
            })
            .catch( err =>
                console.log(err)
            );
    }

    handleSubmit = (e) => {
        e.preventDefault();
        const url = `/api/restaurants`;
        const data = {...this.state};
        axios.post(url, data)
            .then( res => {
                this.clearForm();
                console.log(res);
                if(res.data.status.status){
                    this.props.onResponse({success:true, successMsg: res.data.status.msg});
                    this.props.history.push('/restaurants')
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
        const {name, cuisineID} = this.state;
        return name.length > 0 && cuisineID != null;
    };

    clearForm = () => {
        this.setState({name: "", cuisineID: null});
    };

    render(){
        const {name, cuisineID, cuisines} = this.state;
        return (
            <div>
                <h1>Create Restaurant</h1>
                <Form onSubmit={this.handleSubmit}>
                    <FormGroup>
                        <FormLabel>Title</FormLabel>
                        <FormControl type="text" name="name" placeholder="Enter Your Restaurant Name" value={name} onChange={this.handleChange}/>
                    </FormGroup>
                    <FormGroup>
                        <FormLabel>Cuisine</FormLabel><br/>
                        <ButtonGroup>
                            <Cuisines selectedCuisine={cuisineID} cuisines={cuisines} handleClick={this.handleClick}/>
                        </ButtonGroup>
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

export default withRouter(RestaurantForm)

