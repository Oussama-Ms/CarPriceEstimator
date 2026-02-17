import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LandingPage from './components/LandingPage';
import PredictionForm from './components/PredictionForm';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<LandingPage />} />
                <Route path="/predict" element={<PredictionForm />} />
            </Routes>
        </Router>
    );
}

export default App;
