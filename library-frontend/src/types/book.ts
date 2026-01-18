export enum ReadingStatus {
  TO_READ = "TO_READ",
  CURRENTLY_READING = "CURRENTLY_READING",
  FINISHED = "FINISHED",
  DNF = "DNF"
}

export interface Book {
  id: number;
  isbn?: string;
  title: string;
  author?: string;
  description?: string;
  publishedDate?: string;
  pageCount?: number;
  thumbnailUrl?: string;
  status: ReadingStatus;
  finishedDate?: string;
  rating?: number;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateBookRequest {
  title: string;
  author?: string;
  isbn?: string;
  description?: string;
  publishedDate?: string;
  pageCount?: number;
  thumbnailUrl?: string;
  status?: ReadingStatus;
  notes?: string;
}

export interface UpdateBookRequest {
  title?: string;
  author?: string;
  description?: string;
  status?: ReadingStatus;
  finishedDate?: string;
  rating?: number;
  notes?: string;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}