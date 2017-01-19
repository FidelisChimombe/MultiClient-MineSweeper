package minesweeper;

/*
 * Board data type, represents the board in Mine Sweeper game
 * 
 * Thread safety Argument
 * The datatype is thread-safe it uses implicit locks on methods that write a shared resource.
 */
public class Board {
	
	private final int N;
	private Square [][] board;
	public Board(int n){
		this.N=n;
		this.board=new Square [N][N];
		//i is for x coordinate
		//y is for y coordinate
		//board [x][y]
		for (int i=0;i<this.N;i++){
			for (int j=0;j<this.N;j++){
				double prob=Math.random();
				if(prob>0.25){
					//no bomb
					this.board[i][j]=new Square(i,j,"untouched",false);
				}else{
					//bomb
					this.board[i][j]=new Square(i,j,"untouched",true);
				}
			}
		}
		
	}
	
	public int getBoardSize(){
		return this.N;
	}
	
	public Square [][] getBoad(){
		return this.board;
	}
	
	 /* counts the number of neighbors a square has
	  * @param x, an int representing the x coordinate of a square
	  * @param y, an int representing the x coordinate of a square
	  * @return , a string which is the current board representation
	  */
	public int getNeighborsWithBomb(int x,int y){
		int neighbors=0;
		for(int i=-1;i<2;i++){
			for(int j=-1;j<2;j++){
				if(i!=0 && j!=0){
					//you will be in the square that is surrounded by neighbors
				}else{
					if(x+i>=0 && x+i<this.getBoardSize() && y+j>=0 && y+j<this.getBoardSize()){
						Square sq=this.getBoad()[x+i][y+j];
						if(sq.isBomb()){
							neighbors++;
						}
					}
				}
			}
		}
		return neighbors;
	}
	
	/*
	 * prints the string representation of the current state of the board.
	 */
	public synchronized String look(){
		String current_state="";
		for(int i=0;i<this.getBoardSize();i++){
			for (int j=0;j<this.getBoardSize();j++){
			
					if(this.getBoad()[i][j].getState()=="untouched"){
						current_state=current_state.concat("-");
					}else if(this.getBoad()[i][j].getState()=="flagged"){
						current_state=current_state.concat("F");
					}else if(this.getBoad()[i][j].getState()=="dug"){
						current_state=current_state.concat(" ");
					}else{
						//do nothing
					}
				
			}
			current_state=current_state.concat("\n");
		}
		
		return current_state;
	}
	
	/* digs a given square
	 *  @param x, an int representing the x coordinate of a square
	 * @param y, an int representing the x coordinate of a square
	 * @return , a string which is the current board representation
	 */
	public synchronized void dig(int x,int y){
		if((x>=0 && x<this.getBoardSize()) && (y>=0 && y<this.getBoardSize())){
			if(this.board[x][y].getState()=="untouched" && this.getNeighborsWithBomb(x, y)!=0){
				this.board[x][y]=new Square(x,y,"dug",false);
			}
		} 
		
		
	}
	
	/*
	 *recursive digging, when there is no bomb in a square, and the square is not surrounded by no bombs
	 * @param x, an int representing the x coordinate of a square
	 * @param y, an int representing the x coordinate of a square
	 * @return , a string which is the current board representation
	 */
	 
	public synchronized void recursiveDig(int x,int y){
		//dig doesn't need to send a message to the client if they did a bomb, i should check 
		//recursive in the case when non of the neighbors
		//dig is synchronized because it manipulates a shared resource board.
		
		if((x>=0 && x<this.getBoardSize()) && (y>=0 && y<this.getBoardSize())){
			if(this.board[x][y].getState()=="untouched" && this.getNeighborsWithBomb(x, y)==0){
				for(int i=-1;i<2;i++){
					for(int j=-1;j<2;j++){
						if((x+i>=0 && x+i<this.getBoardSize()) && (y+j>=0 && y+j<this.getBoardSize())){
							this.board[x+i][y+j]=new Square(x,y,"dug",false);
						}
					}
				}
				
			}
		}
		
		
	}
	

	/*
	 * flags a square when given x,y coordinates
	 * @param x, an int representing the x coordinate of a square
	 * @param y, an int representing the x coordinate of a square
	 * @return , a string which is the current board representation
	 */
	public synchronized String flag(int x,int y){
		if((x>=0 && x<this.getBoardSize()) && (y>=0 && y<this.getBoardSize())){
			if(this.board[x][y].getState()=="untouched"){
				this.board[x][y]=new Square(x,y,"flagged",false);
			}
		} 
		
		return look();
	}
	
	/*
	 * deflags a square when given x,y coordinates
	 * @param x, an int representing the x coordinate of a square
	 * @param y, an int representing the x coordinate of a square
	 * @return , a string which is the current board representation
	 */
	public synchronized String deflag(int x, int y){
		if((x>=0 && x<this.getBoardSize()) && (y>=0 && y<this.getBoardSize())){
			if(this.board[x][y].getState()=="flagged"){
				this.board[x][y]=new Square(x,y,"untouched",false);
			}
		} 
		
		return look();
	}
	/*
	 * @return, a string which contains help commands
	 */
	public String help_request(){
		String help_message="Here are the messages you can send to the server\n"+
							"LOOK :=="+ "'look'\n"+
							"DIG :== 'dig' SPACE X SPACE Y\n"+
							"FLAG :== 'flag' SPACE X SPACE Y\n"+
							"DEFLAG :== 'deflag' SPACE X SPACE Y\n"+
							"HELP_REQ :== 'help'";
		return help_message;
	}
	
	
	
	
}
