const arr = [];
var canCross = function (row, col, cells, day) {
    const visited = [];
    const queue = [];
    const set = new Set();
    const directions = [[0, 1], [1, 0], [0, -1], [-1, 0]];
    for (let i = 0; i < day; i++) {
        visited[cells[i][0], cells[i][1]] = false;
        queue.push([cells[i][0], cells[i][1]]);
    }
    while (queue.length > 0) {
        let [x, y] = queue.shift();
        for (let i =0; i< directions.length; i++) {
            x = x + directions[i][0];
            y = y + directions[i][1];
            if (x >=0 && x < row && y >=0 && y < col )
        }
    }


    return false;
}
var latestDayToCross = function (row, col, cells) {
    const visited = [];


};

latestDayToCross(2, 2, [[1, 2], [2, 1], [1, 2], [2, 2]]);