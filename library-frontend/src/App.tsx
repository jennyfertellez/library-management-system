import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import DashboardPage from './pages/DashboardPage';
import BookListPage from './pages/BookListPage';
import ShelvesPage from './pages/ShelvesPage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<DashboardPage />} />
          <Route path="books" element={<BookListPage />} />
          <Route path="shelves" element={<ShelvesPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;