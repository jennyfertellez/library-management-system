import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import DashboardPage from './pages/DashboardPage';
import BookListPage from './pages/BookListPage';
import ShelvesPage from './pages/ShelvesPage';
import BookDetailPage from './pages/BookDetailPage';
import { ThemeProvider } from './contexts/ThemeContext';

function App() {
  return (
    <ThemeProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<DashboardPage />} />
            <Route path="books" element={<BookListPage />} />
            <Route path="books/:id" element={<BookDetailPage />} />
            <Route path="shelves" element={<ShelvesPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;