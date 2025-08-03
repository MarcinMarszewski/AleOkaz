import { useState } from 'react'
import { HashRouter, Routes, Route, Link } from 'react-router-dom'
import './App.css'
import LoginPage from './pages/LoginPage'
import UserProfilePage from './pages/UserProfilePage'
import FishingSpotsPage from './pages/FishingSpotsPage'
import FishingSpotPostsPage from './pages/FishingSpotPostsPage'

function Navbar() {
  return (
    <nav style={{
      background: '#222',
      color: '#fff',
      padding: '1rem',
      marginBottom: '2rem',
      display: 'flex',
      gap: '1.5rem',
      alignItems: 'center',
    }}>
      <Link to="/user-profile" style={{ color: '#fff', textDecoration: 'none', fontWeight: 'bold' }}>Profile</Link>z
      <Link to="/fishing-spots" style={{ color: '#fff', textDecoration: 'none' }}>Fishing Spots</Link>
      <Link to="/login" style={{ color: '#fff', textDecoration: 'none', marginLeft: 'auto' }}>Logout</Link>
    </nav>
  );
}

function App() {
  const [count, setCount] = useState(0)

  return (
    <HashRouter>
      <Navbar />
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/user-profile" element={<UserProfilePage isMe={true}/>} />
        <Route path="/user-profile/:usernameParam" element={<UserProfilePage />} />
        <Route path="/fishing-spots" element={<FishingSpotsPage />} />
        <Route path="/fishing-spots/:fishingSpotId/posts" element={<FishingSpotPostsPage />} />
      </Routes>
    </HashRouter>
  )
}

export default App
