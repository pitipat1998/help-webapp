import React, {Component} from "react";
import {withRouter} from "react-router-dom";
import axios from "../utils/axiosConfig";
import {Table, Button, Form, FormLabel, ButtonGroup, DropdownButton, Dropdown} from "react-bootstrap";

const mapping = new Map([[1,"Awful!"], [2, "Mehh"], [3, "Nice"], [4, "Good"], [5, "Epic"]]);

const DropDownRating = (props) => {
    const ls = [];
    mapping.forEach((value, key, map) => {
            ls.push(<Dropdown.Item key={key} name={props.review.id} value={key} onClick={() => props.handleUpdate(key, props.review)}>{value}</Dropdown.Item>)
    });
    return ls;
};

const ReviewRow = (props) => {
    return props.reviews.map( review => (
          <tr key={review.id}>
              <td>{review.ratedBy}</td>
              {
                  (props.user === review.ratedBy && props.isEdited === review.id) ?
                      <td><DropdownButton title="Rating"><DropDownRating review={review} handleUpdate={props.handleUpdate}/></DropdownButton></td>
                      :
                      <td>{mapping.get(review.rating)}</td>
              }
              <td>
              <Button name="isEdited" value={review.id} onClick={props.handleClick} disabled={props.user !== review.ratedBy}>Edit</Button>
              <Button onClick={() => props.handleDelete(review)} disabled={props.user !== review.ratedBy}>Delete</Button>
              </td>
          </tr>
    ));
};

class Restaurant extends Component{

    state = {
        name: "",
        globalRating: 0.0,
        ratingCount: 0,
        cuisine: "",
        createdBy: "",
        reviews: [],
        user: "",
        userRating: null,
        ratedReview: null,
        deletedReview: null,
        isEdited : null,
        alreadyReview: false
    };

    componentWillMount(){
        this.fetchData();
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if(prevState.ratedReview !== this.state.ratedReview ||
            prevState.deletedReview !== this.state.deletedReview ||
            prevState.isEdited !== this.state.isEdited){
            this.fetchData();
        }
    }

    fetchData = () => {
        const { match: { params } } = this.props;
        axios.get(`/api/restaurants/${params.id}`)
            .then(res => {
                    if (res.data.payload == null) {
                        this.props.history.push("/sign-in")
                    } else {
                        this.props.onAuth();
                        if(res.data.status.status){
                            const restaurantInfo = {...res.data.payload[0]};
                            const reviewsList = res.data.payload[1];
                            this.setState({
                                ...restaurantInfo,
                                reviews: reviewsList,
                                alreadyReview: res.data.payload[2],
                                user: res.data.status.user
                            });
                        }
                        else{
                            this.props.onResponse({err:true, errMsg:res.data.status.msg})
                        }
                    }
                }
            )
            .catch(err => console.log(err))
    };

    handleRestaurantDelete = () => {
        const { match: { params } } = this.props;
        axios.delete(`/api/restaurants/${params.id}`)
            .then(res => {
                    console.log(res);
                    if(res.data.status.status){
                        this.props.onResponse({success:true, successMsg:res.data.status.msg});
                        this.props.history.push("/restaurants")
                    }
                    else
                        this.props.onResponse({err:true, errMsg: res.data.status.msg});
                }
            )
            .catch(err => console.log(err))
    };

    handleReviewDelete = (review) => {
        axios.delete(`/api/reviews/${review.id}`)
            .then(res => {
                    console.log(res);
                    if(res.data.status.status){
                        this.props.onResponse({success:true, successMsg:res.data.status.msg});
                        this.setState({deletedReview: review.id})
                    }
                    else
                        this.props.onResponse({err:true, errMsg: res.data.status.msg});
                }
            )
            .catch(err => console.log(err))
    };

    handleReviewUpdate = (e, review) => {
        axios.patch(`/api/reviews/${review.id}`, {rating: parseInt(e)})
            .then(res => {
                    console.log(res);
                    if(res.data.status.status){
                        this.props.onResponse({success:true, successMsg:res.data.status.msg});
                        this.setState({isEdited: null})
                    }
                    else
                        this.props.onResponse({err:true, errMsg: res.data.status.msg});
                }
            )
            .catch(err => console.log(err))
    };

    handleSubmit = (e) => {
        e.preventDefault();
        const { match: { params } } = this.props;
        const data = {rating: this.state.userRating};
        axios.post(`/api/reviews/${params.id}`, data)
            .then(res => {
                    console.log(res);
                    if(res.data.status.status){
                        this.props.onResponse({success:true, successMsg:res.data.status.msg});
                        this.setState({ratedReview: res.data.payload, userRating: null})
                    }
                    else
                        this.props.onResponse({err:true, errMsg: res.data.status.msg});
                }
            )
            .catch(err => console.log(err))
    };


    handleClick = (e) => {
        console.log(e.target.name, e.target.value);
        this.setState({[e.target.name]: parseInt(e.target.value)})
    };

    render() {
        const { match: { params } } = this.props;
        return (
            <div>
                <h1>Restaurant {params.id}</h1>
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
                        <tr>
                            <td>{this.state.name}</td>
                            <td>{this.state.globalRating}</td>
                            <td>{this.state.ratingCount}</td>
                            <td>{this.state.cuisine}</td>
                            <td>{this.state.createdBy}</td>
                        </tr>
                    </tbody>
                </Table>
                {(this.state.createdBy !== this.state.user) ? null : <Button onClick={this.handleRestaurantDelete}>Delete</Button>}
                <h1>Reviews</h1>
                <Table striped bordered hover variant="dark">
                    <thead>
                    <tr>
                        <th>Rated By</th>
                        <th>Rating</th>
                    </tr>
                    </thead>
                    <tbody>
                        <ReviewRow isEdited={this.state.isEdited} handleUpdate={this.handleReviewUpdate} handleClick={this.handleClick} handleDelete={this.handleReviewDelete} user={this.state.user} reviews={this.state.reviews}/>
                    </tbody>
                </Table>

                {(this.state.alreadyReview) ?
                    null
                    :
                    <div>
                        <h1>Leave a Review</h1>
                            <Form onSubmit={this.handleSubmit}>
                                <FormLabel>Rating</FormLabel><br/>
                                <ButtonGroup>
                                    <Button name="userRating" value={1} active={this.state.userRating === 1} onClick={this.handleClick}>{mapping.get(1)}</Button>
                                    <Button name="userRating" value={2} active={this.state.userRating === 2} onClick={this.handleClick}>{mapping.get(2)}</Button>
                                    <Button name="userRating" value={3} active={this.state.userRating === 3} onClick={this.handleClick}>{mapping.get(3)}</Button>
                                    <Button name="userRating" value={4} active={this.state.userRating === 4} onClick={this.handleClick}>{mapping.get(4)}</Button>
                                    <Button name="userRating" value={5} active={this.state.userRating === 5} onClick={this.handleClick}>{mapping.get(5)}</Button>
                                </ButtonGroup><br />
                                <Button disabled={this.state.userRating === null} type="submit">Submit</Button>
                            </Form>
                    </div>
                }
            </div>
        );
    }

}
export default withRouter(Restaurant)
