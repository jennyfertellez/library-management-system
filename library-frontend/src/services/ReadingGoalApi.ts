import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

export interface ReadingGoal {
  id: number;
  targetBooks: number;
  year: number;
  startDate: string; // ISO date string
  endDate: string;
  description?: string;
  isActive: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateGoalRequest {
  targetBooks: number;
  year: number;
  startDate: string;
  endDate: string;
  description?: string;
}

export interface UpdateGoalRequest {
  targetBooks?: number;
  startDate?: string;
  endDate?: string;
  description?: string;
  isActive?: boolean;
}

export interface GoalProgress {
  goalId: number;
  targetBooks: number;
  year: number;
  startDate: string;
  endDate: string;
  description?: string;
  isActive: boolean;

  // Progress metrics
  booksRead: number;
  booksRemaining: number;
  percentageComplete: number;

  // Time metrics
  daysElapsed: number;
  daysRemaining: number;
  totalDays: number;

  // Pace metrics
  booksPerMonth: number;
  booksPerWeek: number;
  averageBooksPerMonth: number;
  onTrack: boolean;

  // Recently finished books
  recentlyFinished: Array<{
    id: number;
    title: string;
    author?: string;
    thumbnail?: string;
    finishedDate: string;
  }>;
}

const ReadingGoalApi = {
  // Get all goals
  getAllGoals: async (): Promise<ReadingGoal[]> => {
    const response = await axios.get(`${API_BASE_URL}/goals`);
    return response.data;
  },

  // Get goal by ID
  getGoalById: async (id: number): Promise<ReadingGoal> => {
    const response = await axios.get(`${API_BASE_URL}/goals/${id}`);
    return response.data;
  },

  // Get active goal
  getActiveGoal: async (): Promise<ReadingGoal> => {
    const response = await axios.get(`${API_BASE_URL}/goals/active`);
    return response.data;
  },

  // Get current goal
  getCurrentGoal: async (): Promise<ReadingGoal> => {
    const response = await axios.get(`${API_BASE_URL}/goals/current`);
    return response.data;
  },

  // Create new goal
  createGoal: async (request: CreateGoalRequest): Promise<ReadingGoal> => {
    const response = await axios.post(`${API_BASE_URL}/goals`, request);
    return response.data;
  },

  // Update goal
  updateGoal: async (id: number, request: UpdateGoalRequest): Promise<ReadingGoal> => {
    const response = await axios.put(`${API_BASE_URL}/goals/${id}`, request);
    return response.data;
  },

  // Delete goal
  deleteGoal: async (id: number): Promise<void> => {
    await axios.delete(`${API_BASE_URL}/goals/${id}`);
  },

  // Get goal progress
  getGoalProgress: async (id: number): Promise<GoalProgress> => {
    const response = await axios.get(`${API_BASE_URL}/goals/${id}/progress`);
    return response.data;
  },
};

export default ReadingGoalApi;