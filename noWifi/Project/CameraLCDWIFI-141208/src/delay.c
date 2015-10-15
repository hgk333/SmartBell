#include "stm32f4xx.h"

static uint32_t TimingDelay;
extern uint32_t SystemCoreClock;

void SysTick_Configuration(void)
{
    printf("\r\nSystemCoreClock:%d\r\n\r\n",SystemCoreClock);
	if(SysTick_Config(SystemCoreClock / 1000)) {
        while(1);
    }
}

void mdelay(u32 ms)
{
    unsigned int i, j;
	
	for(i=0; i<ms; i++)
	{
		for(j=0; j<2000; j++)		{}
}
}

void delay_ms(unsigned int ms)
{
	unsigned int i, j;
	
	for(i=0; i<ms; i++)
	{
		for(j=0; j<2000; j++)		{}
	}
}
/*
void TimingDelay_Decrement(void)
{
    if (TimingDelay != 0x00) {
        TimingDelay--;
    }
}
*/

/**
  * @brief  This function handles SysTick Handler.
  * @param  None
  * @retval None
  */

/*
void SysTick_Handler(void)
{
    TimingDelay_Decrement();
}
*/
