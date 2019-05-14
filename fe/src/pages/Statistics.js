import React, {Component} from "react";
import axios from "../utils/axiosConfig";
import {Table} from "react-bootstrap";

const UserRow = (props) => {
    return props.users.map( (user,idx) => (
          <tr key={user.username}>
              <td>{idx+1}</td>
              <td>{user.username}</td>
              <td>{user.totMembership}</td>
          </tr>
    ));
};


class Statistics extends Component{

    state = {
        totRestaurant: null,
        totUsers: null,
        totRatings: null,
        avgRatingPerRestaurant: null,
        avgRatingPerUser: null,
        totParties: null,
        avgUserPerParty: null,
        topMembers: []
    };

    componentDidMount() {
        axios.get("/api/statistics")
             .then( res => {
                console.log(res);
                if (res.data.payload == null) {
                    this.props.history.push("/sign-in")
                }
                else {
                    this.props.onAuth();
                    this.setState({
                        ...res.data.payload
                    });
                }
            })
            .catch( err =>
                console.log(err)
            );
    }

    render() {
        return (
            <div>
                <h3><strong>Total Restaurants</strong> <p>{this.state.totRestaurant}</p></h3>
                <h3><strong>Total Users </strong><p>{this.state.totUsers}</p></h3>
                <h3><strong>Total Ratings </strong><p>{this.state.totRatings}</p></h3>
                <h3><strong>Average Rating Per Restaurant</strong> <p>{this.state.avgRatingPerRestaurant}</p></h3>
                <h3><strong>Average Rating Per User </strong><p>{this.state.avgRatingPerUser}</p></h3>
                <h3><strong>Total Parties </strong><p>{this.state.totParties}</p></h3>
                <h3><strong>Average User Per Party</strong> <p>{this.state.avgUserPerParty}</p></h3>
                <h3><strong>Top 10 Popular Users </strong></h3>
                <Table>
                    <thead>
                        <tr>
                            <td>Rank</td>
                            <td>Username</td>
                            <td>Number of Memberships</td>
                        </tr>
                    </thead>
                    <tbody>
                        <UserRow users={this.state.topMembers}/>
                    </tbody>
                </Table>
            </div>
        );
    }
}

export default Statistics