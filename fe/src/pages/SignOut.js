import React, {Component} from "react";
import {withRouter} from "react-router-dom";
import axios from "../utils/axiosConfig";

class SignOut extends Component{
    componentWillMount() {
        const url = "/api/sign-out";
        axios.get(url)
            .then(res => {
                    if(res.data.status){
                        this.props.onResponse({success:true, successMsg: res.data.msg});
                    }
                    else{
                        this.props.onResponse({err:true, errMsg: res.data.msg});
                    }
                    this.props.history.push("/sign-in");
                    console.log(res);
                }
            )
            .catch(err => console.log(err));
    }
    render(){
        return (<></>);
    }
}
export default withRouter(SignOut);