import type { Book } from '../types/book';

export interface Shelf {
    id: number;
    name: string;
    description?: string;
    bookCount: number;
    books: Book[];
    createdAt: string;
    }

export interface CreateShelfRequest {
    name: string;
    description?: string;
    }

export interface UpdateShelfRequest {
    name?: string;
    description?: string;
    }