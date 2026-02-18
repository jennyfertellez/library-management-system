import React, { useState, useEffect } from 'react';
import { Target, Plus, TrendingUp, AlertCircle } from 'lucide-react';
import ReadingGoalApi, {
    type ReadingGoal
} from '../services/ReadingGoalApi';
import ReadingGoalProgressCard from '../components/ReadingGoalProgressCard';
import CreateReadingGoalModal from '../components/CreateReadingGoalModal';

const GoalsPage: React.FC = () => {
  const [goals, setGoals] = useState<ReadingGoal[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingGoal, setEditingGoal] = useState<ReadingGoal | null>(null);
  const [deleteConfirm, setDeleteConfirm] = useState<number | null>(null);

  useEffect(() => {
    fetchGoals();
  }, []);

  const fetchGoals = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await ReadingGoalApi.getAllGoals();
      setGoals(data);
    } catch (err) {
      console.error('Failed to fetch goals:', err);
      setError('Failed to load goals. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateClick = () => {
    setEditingGoal(null);
    setIsModalOpen(true);
  };

  const handleEditClick = (goal: ReadingGoal) => {
    setEditingGoal(goal);
    setIsModalOpen(true);
  };

  const handleDeleteClick = (goalId: number) => {
    setDeleteConfirm(goalId);
  };

  const handleConfirmDelete = async () => {
    if (!deleteConfirm) return;

    try {
      await ReadingGoalApi.deleteGoal(deleteConfirm);
      setGoals((prev) => prev.filter((g) => g.id !== deleteConfirm));
      setDeleteConfirm(null);
    } catch (err) {
      console.error('Failed to delete goal:', err);
      alert('Failed to delete goal. Please try again.');
    }
  };

  const handleModalClose = () => {
    setIsModalOpen(false);
    setEditingGoal(null);
  };

  const handleModalSuccess = () => {
    fetchGoals();
  };

  // Loading State
  if (loading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex items-center gap-3 mb-6">
          <Target className="w-8 h-8 text-blue-600 dark:text-blue-400" />
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
            Reading Goals
          </h1>
        </div>

        <div className="grid gap-6 md:grid-cols-2">
          {[1, 2].map((n) => (
            <div key={n} className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 animate-pulse">
              <div className="h-6 bg-gray-200 dark:bg-gray-700 rounded w-1/3 mb-4"></div>
              <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-full mb-2"></div>
              <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-2/3"></div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  // Error State
  if (error) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex items-center gap-3 mb-6">
          <Target className="w-8 h-8 text-blue-600 dark:text-blue-400" />
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
            Reading Goals
          </h1>
        </div>

        <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-6">
          <div className="flex items-center gap-3">
            <AlertCircle className="w-6 h-6 text-red-600 dark:text-red-400" />
            <div>
              <h3 className="text-lg font-semibold text-red-900 dark:text-red-200">
                Error Loading Goals
              </h3>
              <p className="text-red-700 dark:text-red-300">{error}</p>
              <button
                onClick={fetchGoals}
                className="mt-3 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
              >
                Try Again
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center gap-3">
          <Target className="w-8 h-8 text-blue-600 dark:text-blue-400" />
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
            Reading Goals
          </h1>
        </div>

        <button
          onClick={handleCreateClick}
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          <Plus className="w-5 h-5" />
          New Goal
        </button>
      </div>

      {/* Goals Grid */}
      {goals.length === 0 ? (
        // Empty State
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-12 text-center">
          <TrendingUp className="w-16 h-16 text-gray-400 dark:text-gray-600 mx-auto mb-4" />
          <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
            No Reading Goals Yet
          </h3>
          <p className="text-gray-600 dark:text-gray-400 mb-6">
            Create your first reading goal to start tracking your progress!
          </p>
          <button
            onClick={handleCreateClick}
            className="inline-flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            <Plus className="w-5 h-5" />
            Create Your First Goal
          </button>
        </div>
      ) : (
        <div className="grid gap-6 md:grid-cols-2">
          {goals.map((goal) => (
            <ReadingGoalProgressCard
              key={goal.id}
              goal={goal}
              onEdit={handleEditClick}
              onDelete={handleDeleteClick}
            />
          ))}
        </div>
      )}

      {/* Create/Edit Modal */}
      <CreateReadingGoalModal
        isOpen={isModalOpen}
        onClose={handleModalClose}
        onSuccess={handleModalSuccess}
        editGoal={editingGoal}
      />

      {/* Delete Confirmation Modal */}
      {deleteConfirm !== null && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-md w-full p-6">
            <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-3">
              Delete Reading Goal?
            </h3>
            <p className="text-gray-600 dark:text-gray-400 mb-6">
              Are you sure you want to delete this goal? This action cannot be undone.
            </p>
            <div className="flex gap-3">
              <button
                onClick={() => setDeleteConfirm(null)}
                className="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700"
              >
                Cancel
              </button>
              <button
                onClick={handleConfirmDelete}
                className="flex-1 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
              >
                Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default GoalsPage;