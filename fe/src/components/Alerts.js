import React from "react";
import {Alert, Button} from "react-bootstrap";

export const Alerts = (props) => (
    <Alert show={props.show} variant={props.status}>
        <Alert.Heading>{props.heading}</Alert.Heading>
        <p>
            {props.msg}
        </p>
        <hr />
        <div className="d-flex justify-content-end">
            <Button onClick={props.handleHide} variant={`outline-${props.status}`}>
                Close me ya'll!
            </Button>
        </div>
    </Alert>
);