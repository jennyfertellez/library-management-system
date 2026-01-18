import { api } from './api';
import { Shelf, CreateShelfRequest, UpdateShelfRequest } from '../types/shelf';

export const shelfService = {
  // Get all shelves
  getAllShelves: async () => {
    const response = await api.get<Shelf[]>('/shelves');
    return response.data;
  },

  // Get shelf by ID
  getShelfById: async (id: number) => {
    const response = await api.get<Shelf>(`/shelves/${id}`);
    return response.data;
  },

  // Create shelf
  createShelf: async (shelf: CreateShelfRequest) => {
    const response = await api.post<Shelf>('/shelves', shelf);
    return response.data;
  },

  // Update shelf
  updateShelf: async (id: number, shelf: UpdateShelfRequest) => {
    const response = await api.put<Shelf>(`/shelves/${id}`, shelf);
    return response.data;
  },

  // Delete shelf
  deleteShelf: async (id: number) => {
    await api.delete(`/shelves/${id}`);
  },

  // Add book to shelf
  addBookToShelf: async (shelfId: number, bookId: number) => {
    const response = await api.post<Shelf>(`/shelves/${shelfId}/books/${bookId}`);
    return response.data;
  },

  // Remove book from shelf
  removeBookFromShelf: async (shelfId: number, bookId: number) => {
    const response = await api.delete<Shelf>(`/shelves/${shelfId}/books/${bookId}`);
    return response.data;
  },
};