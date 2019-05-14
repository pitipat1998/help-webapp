import React, {Component} from "react";
import axios from "../utils/axiosConfig"
import {withRouter} from "react-router-dom";
import {Table, Button} from "react-bootstrap";

const RestaurantRow = (props) => {
    return props.parties.map( party => (
          <tr key={party.id}>
              <td onClick={() => props.handleClick(party)}>{party.name}</td>
              <td onClick={() => props.handleClick(party)}>{party.noMembers}</td>
          </tr>
    ));
};

class Parties extends Component{

    state = {
        userJoined : [],
        userUnjoined : []
    };

    componentWillMount() {
        this.fetchData()
    }

    fetchData = () => {
        axios.get("/api/parties")
            .then(res => {
                    console.log(res);
                    if (res.data.payload == null) {
                        this.props.history.push("/sign-in")
                    } else {
                        this.props.onAuth();
                        this.setState({
                            userJoined: res.data.payload[0],
                            userUnjoined: res.data.payload[1]
                        });
                    }
                }
            )
            .catch(err => console.log(err))
    };

    handleClick = (party) => {
        this.props.history.push(`/parties/${party.id}`)
    };

    render() {
        return (
            <div>
                <h1>Joined Parties</h1>
                <Table striped bordered hover variant="dark">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Total Members</th>
                    </tr>
                    </thead>
                    <tbody>
                        <RestaurantRow parties={this.state.userJoined} handleClick={this.handleClick}/>
                    </tbody>
                </Table>
                <h1>Other Parties</h1>
                <Table striped bordered hover variant="dark">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Total Members</th>
                    </tr>
                    </thead>
                    <tbody>
                        <RestaurantRow parties={this.state.userUnjoined} handleClick={this.handleClick}/>
                    </tbody>
                </Table>
                <Button onClick={() => this.props.history.push('/party-form')}>Create Party</Button>
            </div>
        );
    }
}

export default withRouter(Parties)