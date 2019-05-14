import React, {Component} from "react";
import axios from "../utils/axiosConfig";
import {withRouter} from "react-router-dom";
import {Table} from "react-bootstrap";

class UserProfile extends Component {

    state = {
        username: "",
        email: "",
        address: "",
        lastLogin: ""
    };

    componentWillMount() {
        axios.get("/api/user-profile")
            .then( res => {
                console.log(res);
                if (res.data.payload == null) {
                    this.props.history.push("/sign-in")
                }
                else {
                    this.props.onAuth();
                    this.setState({
                        username: res.data.payload.username,
                        email: res.data.payload.email,
                        address: res.data.payload.address,
                        lastLogin: res.data.payload.lastLogin
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
                <h1>User Profile</h1>
                <Table striped bordered hover variant="dark">
                    <thead>
                    <tr>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Address</th>
                        <th>Last Login</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>{this.state.username}</td>
                        <td>{this.state.email}</td>
                        <td>{this.state.address}</td>
                        <td>{this.state.lastLogin}</td>
                    </tr>
                    </tbody>
                </Table>
            </div>
        );
    }
}

export default withRouter(UserProfile)