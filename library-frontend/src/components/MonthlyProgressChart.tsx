import React from 'react';
import { BarChart3 } from 'lucide-react';

interface MonthlyProgressChartProps {
  monthlyData: { [key: string]: number };
  targetPerMonth: number;
}

const MonthlyProgressChart: React.FC<MonthlyProgressChartProps> = ({
  monthlyData,
  targetPerMonth
}) => {
  // Sort months chronologically
  const sortedMonths = Object.keys(monthlyData).sort();

  // Find max value for scaling
  const maxBooks = Math.max(...Object.values(monthlyData), targetPerMonth);

  // Get month names
  const getMonthName = (monthKey: string) => {
    const [year, month] = monthKey.split('-');
    const date = new Date(parseInt(year), parseInt(month) - 1);
    return date.toLocaleDateString('en-US', { month: 'short' });
  };

  // Check if month is current
  const isCurrentMonth = (monthKey: string) => {
    const now = new Date();
    const currentKey = now.getFullYear() + '-' + String(now.getMonth() + 1).padStart(2, '0');
    return monthKey === currentKey;
  };

  return (
    <div className="mt-6 p-4 bg-gray-50 dark:bg-gray-700/50 rounded-lg">
      <h4 className="text-sm font-semibold text-gray-700 dark:text-gray-300 mb-4 flex items-center gap-2">
        <BarChart3 className="w-4 h-4" />
        Monthly Progress
      </h4>

      <div className="space-y-3">
        {sortedMonths.map((monthKey) => {
          const count = monthlyData[monthKey];
          const percentage = maxBooks > 0 ? (count / maxBooks) * 100 : 0;
          const isCurrent = isCurrentMonth(monthKey);
          const isAboveTarget = count >= targetPerMonth;

          return (
            <div key={monthKey}>
              <div className="flex items-center justify-between text-xs mb-1">
                <span className={`font-medium ${isCurrent ? 'text-blue-600 dark:text-blue-400' : 'text-gray-600 dark:text-gray-400'}`}>
                  {getMonthName(monthKey)}
                  {isCurrent && ' (Current)'}
                </span>
                <span className="text-gray-700 dark:text-gray-300">
                  {count} {count === 1 ? 'book' : 'books'}
                </span>
              </div>

              {/* Progress bar */}
              <div className="relative w-full bg-gray-200 dark:bg-gray-600 rounded-full h-6 overflow-hidden">
                <div
                  className={`h-full rounded-full transition-all duration-500 ${
                    isAboveTarget
                      ? 'bg-gradient-to-r from-green-500 to-green-600'
                      : 'bg-gradient-to-r from-blue-500 to-blue-600'
                  }`}
                  style={{ width: `${Math.min(percentage, 100)}%` }}
                />

                {/* Target line */}
                {targetPerMonth > 0 && (
                  <div
                    className="absolute top-0 bottom-0 w-0.5 bg-red-500 dark:bg-red-400"
                    style={{ left: `${(targetPerMonth / maxBooks) * 100}%` }}
                    title={`Target: ${targetPerMonth.toFixed(1)} books`}
                  />
                )}
              </div>
            </div>
          );
        })}
      </div>

      {/* Legend */}
      <div className="mt-4 pt-3 border-t border-gray-200 dark:border-gray-600 flex items-center gap-4 text-xs">
        <div className="flex items-center gap-1">
          <div className="w-3 h-3 rounded bg-green-500"></div>
          <span className="text-gray-600 dark:text-gray-400">Above target</span>
        </div>
        <div className="flex items-center gap-1">
          <div className="w-3 h-3 rounded bg-blue-500"></div>
          <span className="text-gray-600 dark:text-gray-400">Below target</span>
        </div>
        <div className="flex items-center gap-1">
          <div className="w-0.5 h-3 bg-red-500"></div>
          <span className="text-gray-600 dark:text-gray-400">Monthly target</span>
        </div>
      </div>
    </div>
  );
};

export default MonthlyProgressChart;