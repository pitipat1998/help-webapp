import React, {Component} from "react";
import {withRouter} from "react-router-dom";
import axios from "../utils/axiosConfig";
import {Table, Button} from "react-bootstrap";

const MemberRow = (props) => {
    return props.members.map( member => (
        <tr key={member.username}>
            <td>{member.username}</td>
        </tr>
    ));
};

const RestaurantRow = (props) => {
    return props.restaurants.map( restaurant => (
        <tr key={restaurant.id}>
            <td>{restaurant.name}</td>
            <td>{restaurant.partyRating}</td>
        </tr>
    ));
};

class Party extends Component{

    state = {
        members: [],
        restaurants: [],
        isMember: false,
        joinedMember: null,
        deletedMember: null,
    };

    componentWillMount(){
        this.fetchData();
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if(prevState.joinedMember !== this.state.joinedMember || prevState.deletedMember !== this.state.deletedMember){
            this.fetchData()
        }
    }

    fetchData = () => {
        const { match: { params } } = this.props;
        axios.get(`/api/parties/${params.id}`)
            .then(res => {
                    console.log(res);
                    if (res.data.payload == null) {
                        this.props.history.push("/sign-in")
                    } else {
                        this.props.onAuth();
                        if(res.data.status.status){
                            const memberships = res.data.payload[0];
                            const partyRestaurants = res.data.payload[1];
                            this.setState({
                                members: memberships,
                                restaurants: partyRestaurants,
                                isMember: res.data.payload[2]
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

    handleJoinParty = () => {
        const { match: { params } } = this.props;
        axios.post(`/api/memberships/${params.id}`)
            .then(res => {
                    if(res.data.status.status){
                        this.props.onResponse({success:true, successMsg: res.data.status.msg});
                        this.setState({
                            joinedMember : res.data.payload
                        });
                    }
                    else{
                        this.props.onResponse({err:true, errMsg:res.data.status.msg})
                    }
                }
            )
            .catch(err => console.log(err))
    };
    handleLeaveParty = () => {
        const { match: { params } } = this.props;
        axios.delete(`/api/memberships/${params.id}`)
            .then(res => {
                    if(res.data.status.status ){
                        this.props.onResponse({success:true, successMsg: res.data.status.msg});
                        if (res.data.payload === 0){
                            this.props.history.push("/parties")
                        }
                        else{
                            this.setState({
                                deletedMember : res.data.payload
                            });
                        }
                    }
                    else{
                        this.props.onResponse({err:true, errMsg:res.data.status.msg})
                    }
                }
            )
            .catch(err => console.log(err))
    };

    render() {
        const { match: { params } } = this.props;
        return (
            <div>
                <h1>Party {params.id}</h1>
                <h2>Members</h2>
                <Table striped bordered hover variant="dark">
                    <thead>
                        <tr>
                            <th>Username</th>
                        </tr>
                    </thead>
                    <tbody>
                        <MemberRow members={this.state.members} />
                    </tbody>
                </Table>
                {(!this.state.isMember) ? <Button onClick={this.handleJoinParty}>Join</Button> : <Button onClick={this.handleLeaveParty}>Leave</Button>}
                <h1>Top 5 Restaurants</h1>
                <Table striped bordered hover variant="dark">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Party Average Rating</th>
                        </tr>
                    </thead>
                    <tbody>
                        <RestaurantRow restaurants={this.state.restaurants} />
                    </tbody>
                </Table>

            </div>
        );
    }

}
export default withRouter(Party)
