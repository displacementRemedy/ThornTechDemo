import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Router } from 'react-router-dom';
import axios from 'axios';

function DemoApp (props) {

    const [file, setFile] = useState(null);
    const [hasHeader, setHasHeader] = useState(false);
    const [historyId, setHistoryId] = useState('');

    function submitForm(contentType, data, setResponse) {
        axios({
            url: "http://localhost:8080/upload-csv-file",
            method: 'POST',
            data: data,
            headers: {
                'Content-Type': contentType
            }
        }).then((response) => {
            setResponse(response.data);
        }).catch((error) => {
            setResponse("error");
        })
    }

    function uploadWithFormData() {
        const formData = new FormData();
        formData.append("file", file);
        formData.append("hasHeader", hasHeader);

        submitForm("multipart/form-data", formData, (msg) => {
            console.log(msg);
            setHistoryId(msg);
        });
    }

    return (

        <div className="App">
            <h2>Upload Form</h2>
            <form>
                <label>
                    File
                    <input type="file" name="file" onChange={(e) => setFile(e.target.files[0])} />
                </label>

                <label>
                    Has Header:
                    <input type="checkbox" name="hasHeader" onChange={(e) => setHasHeader(e.target.value)} />
                </label>

                <input type="button" value="Upload" onClick={uploadWithFormData} />
                    {historyId !== '' && (
                        <Link to={{
                            pathname: '/history',
                            state: {
                                historyId: historyId
                            }
                        }}>View Results</Link>
                    )}
            </form>
        </div>

    )
}

export default DemoApp