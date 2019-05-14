import React from "react";
import {Navbar, Nav} from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.css';

export const NotAuthNav = () => (
    <Navbar bg="light" variant="light">
        <Navbar.Brand href="/">HELP</Navbar.Brand>
        <Nav className="ml-auto">
            <Nav.Link href="/sign-in">Sign In</Nav.Link>
            <Nav.Link href="/sign-up">Sign Up</Nav.Link>
        </Nav>
    </Navbar>
);

export const AuthNav = () => (
    <Navbar bg="light" variant="light">
        <Navbar.Brand href="/">HELP</Navbar.Brand>
        <Nav className="mr-auto">
            <Nav.Link href="/restaurants">Restaurants</Nav.Link>
            <Nav.Link href="/parties">Party</Nav.Link>
            <Nav.Link href="/statistics">Statistics</Nav.Link>
        </Nav>
        <Nav className="ml-auto">
            <Nav.Link href="/sign-out">Sign out</Nav.Link>
        </Nav>
    </Navbar>
);


