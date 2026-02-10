export interface BookSearchResult {
    source: 'openLibrary' | 'google' | 'jikan';
    title: string;
    author?: string;
    description?: string;
    thumbnailUrl?: string;
    publishedDate?: string;
    pageCount?: number;
    isbn?: string;
    sourceId?: string;
    }

export interface MultiSourceSearchResponse {
    query: string;
    results: BookSearchResult[];
    totalResults: number;
    }