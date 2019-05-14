import React, {Component} from "react";
import {withRouter} from "react-router-dom";
import axios from "../utils/axiosConfig";
import {Table, Button} from "react-bootstrap";

const RestaurantRow = (props) => {
    return props.restaurants.map( restaurant => (
          <tr key={restaurant.id}>
              <td onClick={() => props.handleClick(restaurant)}>{restaurant.name}</td>
              <td onClick={() => props.handleClick(restaurant)}>{restaurant.globalRating}</td>
              <td onClick={() => props.handleClick(restaurant)}>{restaurant.ratingCount}</td>
              <td onClick={() => props.handleClick(restaurant)}>{restaurant.cuisine}</td>
              <td onClick={() => props.handleClick(restaurant)}>{restaurant.createdBy}</td>
              <td><Button disabled={restaurant.createdBy !== props.user} onClick={() => props.handleDelete(restaurant)}>
                  Delete
              </Button></td>
          </tr>
    ));
};

class Restaurants extends Component{

    state = {
        ratedRestaurants : [],
        unratedRestaurants : [],
        user: "",
        deletedRestaurant: null
    };

    componentWillMount(){
        this.fetchData();
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if(prevState.deletedRestaurant !== this.state.deletedRestaurant){
            this.fetchData();
        }
    }

    fetchData = () => {
        axios.get("/api/restaurants")
            .then(res => {
                    console.log(res);
                    if (res.data.payload == null) {
                        this.props.history.push("/sign-in")
                    } else {
                        this.props.onAuth();
                        this.setState({
                            ratedRestaurants: res.data.payload[0],
                            unratedRestaurants: res.data.payload[1],
                            user: res.data.status.user
                        });
                    }
                }
            )
            .catch(err => console.log(err))
    };

    handleClick = (restaurant) => {
        this.props.history.push(`/restaurants/${restaurant.id}`)
    };

    handleDelete = (restaurant) => {
        axios.delete(`/api/restaurants/${restaurant.id}`)
            .then(res => {
                console.log(res);
                if (res.data.status.status) {
                    this.props.onResponse({success: true, successMsg: res.data.status.msg});
                    this.setState({deletedRestaurant: restaurant.id});
                } else
                    this.props.onResponse({err: true, errMsg: res.data.status.msg});
            })
            .catch(err => console.log(err))
    };

    render() {
        return (
            <div>
                <h1>User Rated Restaurants</h1>
                <Table striped bordered hover variant="dark">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Global Rating</th>
                        <th>Rating Count</th>
                        <th>Cuisine</th>
                        <th>Created By</th>
                    </tr>
                    </thead>
                    <tbody>
                        <RestaurantRow user={this.state.user} restaurants={this.state.ratedRestaurants} handleClick={this.handleClick} handleDelete={this.handleDelete}/>
                    </tbody>
                </Table>
                <h1>User Unrated Restaurants</h1>
                <Table striped bordered hover variant="dark">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Global Rating</th>
                        <th>Rating Count</th>
                        <th>Cuisine</th>
                        <th>Created By</th>
                    </tr>
                    </thead>
                    <tbody>
                        <RestaurantRow user={this.state.user} restaurants={this.state.unratedRestaurants} handleClick={this.handleClick} handleDelete={this.handleDelete}/>
                    </tbody>
                </Table>
                <Button onClick={() => this.props.history.push('/restaurant-form')}>Create Restaurant</Button>
            </div>
        );
    }

}

export default withRouter(Restaurants)
