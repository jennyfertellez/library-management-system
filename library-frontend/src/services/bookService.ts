import { api } from './api';
import { ReadingStatus } from '../types/book';
import type {
  Book,
  CreateBookRequest,
  UpdateBookRequest,
  PageResponse
} from '../types/book';

export const bookService = {
  // Get all books with pagination
  getAllBooks: async (page = 0, size = 20, sortBy = 'title', direction = 'ASC') => {
    const response = await api.get<PageResponse<Book>>('/books', {
      params: { page, size, sortBy, direction }
    });
    return response.data;
  },

  // Get book by ID
  getBookById: async (id: number) => {
    const response = await api.get<Book>(`/books/${id}`);
    return response.data;
  },

  // Create book
  createBook: async (book: CreateBookRequest) => {
    const response = await api.post<Book>('/books', book);
    return response.data;
  },

  // Create book from ISBN
  createBookFromIsbn: async (isbn: string) => {
    const response = await api.post<Book>(`/books/isbn/${isbn}`);
    return response.data;
  },

  // Update book
  updateBook: async (id: number, book: UpdateBookRequest) => {
    const response = await api.put<Book>(`/books/${id}`, book);
    return response.data;
  },

  // Delete book
  deleteBook: async (id: number) => {
    await api.delete(`/books/${id}`);
  },

  // Search books
  searchBooks: async (term: string, page = 0, size = 20) => {
    const response = await api.get<PageResponse<Book>>('/books/search', {
      params: { term, page, size }
    });
    return response.data;
  },

  // Get books by status
  getBooksByStatus: async (status: string, page = 0, size = 20) => {
    const response = await api.get<PageResponse<Book>>('/books', {
      params: { status, page, size }
    });
    return response.data;
  },

  // Get reading statistics
  getStats: async () => {
    const response = await api.get<ReadingStats>('/books/stats');
    return response.data;
  },
};