import React, { useState } from 'react';
import axios from 'axios';
import { useParams } from 'react-router';

function HistoryView (props) {
    debugger;
    const[result, setResult] = useState('');
    const historyId = props.location.state.historyId;

    axios({
        url: "http://localhost:8080/history",
        method: 'GET',
        params: {
            id: historyId
        },
        headers: {
            'Content-Type': 'application/json'
        }
    }).then((response) => {
        setResult(response.data);
    }).catch((error) => {
        //Do something
    })

    return (
        <ul>
            {/*<li>File Name: {result.fileName}</li>
            <li>Status: {result.status}</li>
            <li>Percent Complete: {result.percentComplete}</li>
            <li>Successful: {result.successful}</li>
            <li>Failed: {result.failed}</li>
            <li>Total: {result.total}</li>*/}
            <li>Result</li>
        </ul>
    )
}

export default HistoryView